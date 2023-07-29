package neostudy.conveyor.service;

import neostudy.conveyor.dto.CreditDTO;
import neostudy.conveyor.dto.EmploymentDTO;
import neostudy.conveyor.dto.PaymentScheduleElement;
import neostudy.conveyor.dto.ScoringDataDTO;
import neostudy.conveyor.dto.enums.EmploymentStatus;
import neostudy.conveyor.dto.enums.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Properties;

@Service
public class ScoringService {

    public static final BigDecimal MIN_AMOUNT;
    public static final BigDecimal MAX_AMOUNT;

    public static final int MIN_TERM;
    public static final int MAX_TERM;

    public static final BigDecimal BASE_RATE;

    public static final BigDecimal DISCOUNT_FOR_SALARY_CLIENT;
    public static final BigDecimal DISCOUNT_FOR_INSURANCE;
    public static final BigDecimal PENALTY_FOR_NO_INSURANCE;

    private static final BigDecimal MAX_SALARIES_NUM = new BigDecimal("20.00");

    private final Logger logger = LoggerFactory.getLogger(ScoringService.class);

    private final PrescoringService prescoringService;

    @Autowired
    public ScoringService(PrescoringService prescoringService) {
        this.prescoringService = prescoringService;
    }

    static {
        BigDecimal penaltyForNoInsurance;
        BigDecimal discountForInsurance;
        BigDecimal discountForSalaryClient;
        BigDecimal baseRate;
        int maxTerm;
        int minTerm;
        BigDecimal maxAmount;
        BigDecimal minAmount;
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("constants.properties"));

            minAmount = new BigDecimal(properties.getProperty("minAmount"));
            maxAmount = new BigDecimal(properties.getProperty("maxAmount"));
            minTerm = Integer.parseInt(properties.getProperty("minTerm"));
            maxTerm = Integer.parseInt(properties.getProperty("maxTerm"));
            baseRate = new BigDecimal(properties.getProperty("baseRate"));
            discountForSalaryClient = new BigDecimal(properties.getProperty("discountForSalaryClient"));
            discountForInsurance = new BigDecimal(properties.getProperty("discountForInsurance"));
            penaltyForNoInsurance = new BigDecimal(properties.getProperty("penaltyForNoInsurance"));
        } catch (Exception e) {
            minAmount = new BigDecimal(10000);
            maxAmount = new BigDecimal(7000000);
            minTerm = 6;
            maxTerm = 60;
            baseRate = new BigDecimal(10);
            discountForSalaryClient = new BigDecimal("2.5");
            discountForInsurance = new BigDecimal("1.7");
            penaltyForNoInsurance = new BigDecimal("3.0");
            e.printStackTrace();
        }
        PENALTY_FOR_NO_INSURANCE = penaltyForNoInsurance;
        DISCOUNT_FOR_INSURANCE = discountForInsurance;
        DISCOUNT_FOR_SALARY_CLIENT = discountForSalaryClient;
        BASE_RATE = baseRate;
        MAX_TERM = maxTerm;
        MIN_TERM = minTerm;
        MAX_AMOUNT = maxAmount;
        MIN_AMOUNT = minAmount;
    }

    public CreditDTO createCredit(ScoringDataDTO scoringData) {
        logger.info("Creating credit for request " + scoringData + " started");

        if (!prescoringService.isAmountValid(scoringData.getAmount()) ||
                !prescoringService.isTermValid(scoringData.getTerm()) ||
                !isBirthdateValid(scoringData.getBirthdate()) ||
                !isEmploymentStatusValid(scoringData.getEmployment()) ||
                !isAmountValid(scoringData.getAmount(), scoringData.getEmployment().getSalary())) {
            logger.info("Scoring data is not valid, empty creditDTO will be return");
            return new CreditDTO();
        }

        CreditDTO credit = new CreditDTO();
        BigDecimal rate = countRate(scoringData);
        BigDecimal monthlyPayment = LoanCalculator.countMonthlyPayment(scoringData.getAmount(), rate, scoringData.getTerm());
        List<PaymentScheduleElement> paymentSchedule = LoanCalculator.createPaymentSchedule(scoringData.getAmount(), rate, scoringData.getTerm(), LocalDate.now().plusMonths(1));

        credit.setRate(rate);
        credit.setMonthlyPayment(monthlyPayment);
        credit.setPaymentSchedule(paymentSchedule);

        return credit;
    }

    private boolean isAmountValid(BigDecimal amount, BigDecimal salary) {
        BigDecimal twentySalaries = salary.multiply(MAX_SALARIES_NUM);
        if (amount.compareTo(twentySalaries) > 0) {
            return false;
        }
        return true;
    }


    public BigDecimal countRate(ScoringDataDTO scoringData) {
        BigDecimal rate = new BigDecimal(BASE_RATE.toString());

        rate = prescoringService.calculateRate(scoringData.getIsSalaryClient(), scoringData.getIsSalaryClient(), rate);
        rate = countRateForEmployment(scoringData.getEmployment(), rate);
        rate = countRateForBirthdateAndGender(scoringData, rate);
        rate = countRateForMaritalStatus(scoringData, rate);

        return rate;
    }

    public BigDecimal countRateForBirthdateAndGender (ScoringDataDTO scoringData, BigDecimal rate) {
        if (scoringData.getGender() == Gender.UNSPECIFIED) {
            rate = rate.add(new BigDecimal("3.00"));
            return rate;
        }

        int age = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();

        if (scoringData.getGender() == Gender.FEMALE) {
            if (age >= 35 && age <= 60) {
                rate = rate.subtract(new BigDecimal("3.00"));
            }
        } else if (scoringData.getGender() == Gender.MALE) {
            if (age >= 30 && age <= 55) {
                rate = rate.subtract(new BigDecimal("3.00"));
            }
        }
        return rate;
    }

    public BigDecimal countRateForMaritalStatus(ScoringDataDTO scoringData, BigDecimal rate) {
        switch (scoringData.getMaritalStatus()) {
            case MARRIED:
                rate = rate.subtract(new BigDecimal("3.00"));
                break;
            case DIVORCED:
                rate = rate.add(new BigDecimal("1.00"));
                break;
        }

        if (scoringData.getDependentAmount() > 1) {
            rate = rate.add(new BigDecimal("1.00"));
        }
        return rate;
    }

    public BigDecimal countRateForEmployment(EmploymentDTO employmentDTO, BigDecimal rate) {
        switch (employmentDTO.getEmploymentStatus()) {
            case SELF_EMPLOYED:
                rate = rate.add(new BigDecimal("1.00"));
                break;
            case BUSINESSMAN:
                rate = rate.add(new BigDecimal("3.00"));
        }
        switch (employmentDTO.getPosition()) {
            case MIDDLE_MANAGER:
                rate = rate.subtract(new BigDecimal("2.00"));
                break;
            case TOP_MANAGER:
                rate = rate.subtract(new BigDecimal("4.00"));
                break;
        }
        return rate;
    }

    public boolean isEmploymentStatusValid(EmploymentDTO employmentDTO) {
        if (employmentDTO.getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            logger.info("Loan request is not valid, user is unemployed");
            return false;
        }

        if (employmentDTO.getWorkExperienceTotal() < 12 || employmentDTO.getWorkExperienceCurrent() < 3) {
            return false;
        }

        return true;
    }

    public boolean isBirthdateValid(LocalDate birthdate) {
        if (Period.between(birthdate, LocalDate.now()).getYears() < 20 ||
                Period.between(birthdate, LocalDate.now()).getYears() > 60) {
            logger.info("Loan request is not valid, user younger than 20 or elder than 60");
            return false;
        }
        return true;
    }

}

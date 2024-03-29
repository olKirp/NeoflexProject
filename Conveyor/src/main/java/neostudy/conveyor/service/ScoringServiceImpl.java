package neostudy.conveyor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import neostudy.conveyor.dto.CreditDTO;
import neostudy.conveyor.dto.EmploymentDTO;
import neostudy.conveyor.dto.PaymentScheduleElement;
import neostudy.conveyor.dto.ScoringDataDTO;
import neostudy.conveyor.dto.EmploymentStatus;
import neostudy.conveyor.dto.Gender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Validated
@RequiredArgsConstructor
public class ScoringServiceImpl implements ScoringService {

    private final LoanOffersService loanOffersService;

    private final LoanCalculatorService loanCalculatorService;

    @Value("${constants.baseRate}")
    private BigDecimal baseRate;

    @Value("${constants.minTerm}")
    private int minTerm;

    @Value("${constants.maxTerm}")
    private int maxTerm;

    @Value("${constants.minAmount}")
    private BigDecimal minAmount;

    @Value("${constants.maxAmount}")
    private BigDecimal maxAmount;

    @Value("${constants.minimalRate}")
    private BigDecimal minimalRate;

    @Value("${constants.maxSalariesNum}")
    private BigDecimal maxSalariesNum;

    @Value("${constants.discountForMarriage}")
    private BigDecimal discountForMarriage;

    @Value("${constants.rateIncreaseForDivorce}")
    private BigDecimal rateIncreaseForDivorce;

    @Value("${constants.rateIncreaseForDependents}")
    private BigDecimal rateIncreaseForDependents;

    @Value("${constants.discountForMiddleManager}")
    private BigDecimal discountForMiddleManager;

    @Value("${constants.discountForTopManager}")
    private BigDecimal discountForTopManager;

    @Value("${constants.rateIncreaseForSelfEmployed}")
    private BigDecimal rateIncreaseForSelfEmployed;

    @Value("${constants.rateIncreaseForBusinessman}")
    private BigDecimal rateIncreaseForBusinessman;

    @Value("${constants.rateIncreaseForUnspecifiedGender}")
    private BigDecimal rateIncreaseForUnspecifiedGender;

    @Value("${constants.discountForAge}")
    private BigDecimal discountForAge;

    public CreditDTO createCredit(ScoringDataDTO scoringData) {
        validateLoanRequest(scoringData);

        BigDecimal amount = loanCalculatorService.calculateAmountWithInsurance(scoringData.getIsInsuranceEnabled(), scoringData.getAmount());
        BigDecimal rate = calculateRate(scoringData);
        BigDecimal monthlyPayment = loanCalculatorService.calculateMonthlyPayment(amount, rate, scoringData.getTerm());
        List<PaymentScheduleElement> paymentSchedule = loanCalculatorService.createPaymentSchedule(amount, rate, scoringData.getTerm(), LocalDate.now().plusMonths(1));

        return CreditDTO.builder()
                .amount(amount)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient())
                .term(scoringData.getTerm())
                .rate(rate)
                .monthlyPayment(monthlyPayment)
                .paymentSchedule(paymentSchedule)
                .psk(loanCalculatorService.calculatePSK(paymentSchedule, amount, scoringData.getTerm()))
                .build();
    }

    private void validateLoanRequest(ScoringDataDTO scoringData) {
        Assert.notNull(scoringData, "ScoringDataDTO is null");
        Assert.isTrue(isAmountValid(scoringData.getAmount(), scoringData.getEmployment().getSalary()), "Requested amount less than " + minAmount + " or bigger than " + maxAmount + " or twenty salaries");
        Assert.isTrue(isTermValid(scoringData.getTerm()), "Term longer than " + maxTerm + " or shorter than " + minTerm);
        Assert.isTrue(isBirthdateValid(scoringData.getBirthdate()), "Loan request is not valid, user younger than 20 or elder than 60");
        Assert.isTrue(isEmploymentStatusValid(scoringData.getEmployment()), "Loan request is not valid, user is unemployed or has a total work experience less than 12 or current experience less than 3");
    }

    private boolean isAmountValid(BigDecimal amount, BigDecimal salary) {
        BigDecimal twentySalaries = salary.multiply(maxSalariesNum);
        return isAmountValid(amount) && amount.compareTo(twentySalaries) < 0;
    }

    private BigDecimal calculateRate(ScoringDataDTO scoringData) {
        BigDecimal rate = Optional.of(baseRate)
                .map(it -> loanOffersService.calculateRate(scoringData.getIsSalaryClient(), scoringData.getIsInsuranceEnabled(), it))
                .map(it -> calculateRateForEmployment(scoringData.getEmployment(), it))
                .map(it -> calculateRateForBirthdateAndGender(scoringData, it))
                .map(it -> calculateRateForMaritalStatus(scoringData, it))
                .get();

        if (rate.compareTo(minimalRate) < 0) {
            log.info("Rate is less than minimal rate. Minimal rate was set: " + minimalRate);
            return minimalRate;
        }
        return rate.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRateForBirthdateAndGender(ScoringDataDTO scoringData, BigDecimal rate) {
        log.info("Calculated rate for birthdate and gender. Gender: " + scoringData.getGender() + ", birthdate=" + scoringData.getBirthdate() + ". Initial rate: " + rate);

        if (scoringData.getGender() == Gender.NON_BINARY) {
            return rate.add(rateIncreaseForUnspecifiedGender);
        }

        int age = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();

        if ((scoringData.getGender() == Gender.FEMALE && age >= 35 && age <= 60) || (scoringData.getGender() == Gender.MALE && age >= 30 && age <= 55)) {
            rate = rate.subtract(discountForAge);
        }
        log.info("Calculated rate: " + rate);
        return rate;
    }

    private BigDecimal calculateRateForMaritalStatus(ScoringDataDTO scoringData, BigDecimal rate) {
        log.info("Calculated rate for marital status: " + scoringData.getMaritalStatus() + ", dependents amount=" + scoringData.getDependentAmount() + ". Initial rate: " + rate);

        switch (scoringData.getMaritalStatus()) {
            case MARRIED -> rate = rate.subtract(discountForMarriage);
            case DIVORCED -> rate = rate.add(rateIncreaseForDivorce);
        }

        if (scoringData.getDependentAmount() > 1) {
            rate = rate.add(rateIncreaseForDependents);
        }
        log.info("Calculated rate: " + rate);
        return rate;
    }

    private BigDecimal calculateRateForEmployment(EmploymentDTO employmentDTO, BigDecimal rate) {
        log.info("Calculated rate for employment: " + employmentDTO + ". Initial rate: " + rate);

        switch (employmentDTO.getStatus()) {
            case SELF_EMPLOYED -> rate = rate.add(rateIncreaseForSelfEmployed);
            case BUSINESS_OWNER -> rate = rate.add(rateIncreaseForBusinessman);
        }
        switch (employmentDTO.getEmploymentPosition()) {
            case MID_MANAGER -> rate = rate.subtract(discountForMiddleManager);
            case TOP_MANAGER -> rate = rate.subtract(discountForTopManager);
        }

        log.info("Calculated rate: " + rate);
        return rate;
    }

    private boolean isEmploymentStatusValid(EmploymentDTO employmentDTO) {
        if (employmentDTO.getStatus() == EmploymentStatus.UNEMPLOYED) {
            return false;
        }
        return employmentDTO.getWorkExperienceTotal() >= 12 && employmentDTO.getWorkExperienceCurrent() >= 3;
    }

    private boolean isBirthdateValid(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears() >= 20 &&
                Period.between(birthdate, LocalDate.now()).getYears() <= 60;
    }


    private boolean isAmountValid(BigDecimal amount) {
        return amount.compareTo(minAmount) >= 0 && amount.compareTo(maxAmount) <= 0;
    }

    private boolean isTermValid(Integer term) {
        return term >= minTerm && term <= maxTerm;
    }
}
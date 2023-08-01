package neostudy.conveyor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import neostudy.conveyor.dto.CreditDTO;
import neostudy.conveyor.dto.EmploymentDTO;
import neostudy.conveyor.dto.PaymentScheduleElement;
import neostudy.conveyor.dto.ScoringDataDTO;
import neostudy.conveyor.dto.enums.EmploymentStatus;
import neostudy.conveyor.dto.enums.Gender;
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

    private final PrescoringService prescoringService;

    private final LoanCalculatorService loanCalculatorService;

    private final Constants constants;
    
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
        Assert.isTrue(isAmountValid(scoringData.getAmount(), scoringData.getEmployment().getSalary()), "Requested amount less than " + constants.getMinAmount() + " or bigger than " + constants.getMaxAmount() + " or twenty salaries");
        Assert.isTrue(prescoringService.isTermValid(scoringData.getTerm()), "Term longer than " + constants.getMaxTerm() + " or shorter than " + constants.getMinTerm());
        Assert.isTrue(isBirthdateValid(scoringData.getBirthdate()), "Loan request is not valid, user younger than 20 or elder than 60");
        Assert.isTrue(isEmploymentStatusValid(scoringData.getEmployment()), "Loan request is not valid, user is unemployed or has a total work experience less than 12 or current experience less than 3");
    }

    private boolean isAmountValid(BigDecimal amount, BigDecimal salary) {
        BigDecimal twentySalaries = salary.multiply(constants.getMaxSalariesNum());
        return prescoringService.isAmountValid(amount) && amount.compareTo(twentySalaries) < 0;
    }

    private BigDecimal calculateRate(ScoringDataDTO scoringData) {
        BigDecimal rate = Optional.of(new BigDecimal(constants.getBaseRate().toString()))
                .map(it -> prescoringService.calculateRate(scoringData.getIsSalaryClient(), scoringData.getIsInsuranceEnabled(), it))
                .map(it -> calculateRateForEmployment(scoringData.getEmployment(), it))
                .map(it -> calculateRateForBirthdateAndGender(scoringData, it))
                .map(it -> calculateRateForMaritalStatus(scoringData, it))
                .get();

        if (rate.compareTo(constants.getMinimalRate()) < 0) {
            log.info("Rate is less than minimal rate. Minimal rate was set: " + constants.getMinimalRate());
            return constants.getMinimalRate();
        }
        return rate.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRateForBirthdateAndGender(ScoringDataDTO scoringData, BigDecimal rate) {
        log.info("Calculated rate for birthdate and gender. Gender: " + scoringData.getGender() + ", birthdate=" + scoringData.getBirthdate() + ". Initial rate: " + rate);

        if (scoringData.getGender() == Gender.UNSPECIFIED) {
            return rate.add(new BigDecimal("3.00"));
        }

        int age = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();

        if (scoringData.getGender() == Gender.FEMALE && age >= 35 && age <= 60) {
            rate = rate.subtract(new BigDecimal("3.00"));
        } else if (scoringData.getGender() == Gender.MALE && age >= 30 && age <= 55) {
            rate = rate.subtract(new BigDecimal("3.00"));
        }
        log.info("Calculated rate: " + rate);
        return rate;
    }

    private BigDecimal calculateRateForMaritalStatus(ScoringDataDTO scoringData, BigDecimal rate) {
        log.info("Calculated rate for marital status: " + scoringData.getMaritalStatus() + ", dependents amount=" + scoringData.getDependentAmount() + ". Initial rate: " + rate);

        switch (scoringData.getMaritalStatus()) {
            case MARRIED -> rate = rate.subtract(new BigDecimal("3.00"));
            case DIVORCED -> rate = rate.add(BigDecimal.ONE);
        }

        if (scoringData.getDependentAmount() > 1) {
            rate = rate.add(BigDecimal.ONE);
        }
        log.info("Calculated rate: " + rate);
        return rate;
    }

    private BigDecimal calculateRateForEmployment(EmploymentDTO employmentDTO, BigDecimal rate) {
        log.info("Calculated rate for employment: " + employmentDTO + ". Initial rate: " + rate);

        switch (employmentDTO.getEmploymentStatus()) {
            case SELF_EMPLOYED -> rate = rate.add(BigDecimal.ONE).setScale(2, RoundingMode.HALF_UP);
            case BUSINESSMAN -> rate = rate.add(new BigDecimal("3.00"));
        }
        switch (employmentDTO.getPosition()) {
            case MIDDLE_MANAGER -> rate = rate.subtract(new BigDecimal("2.00"));
            case TOP_MANAGER -> rate = rate.subtract(new BigDecimal("4.00"));
        }

        log.info("Calculated rate: " + rate);
        return rate;
    }

    private boolean isEmploymentStatusValid(EmploymentDTO employmentDTO) {
        if (employmentDTO.getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            return false;
        }
        return employmentDTO.getWorkExperienceTotal() >= 12 && employmentDTO.getWorkExperienceCurrent() >= 3;
    }

    private boolean isBirthdateValid(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears() >= 20 &&
                Period.between(birthdate, LocalDate.now()).getYears() <= 60;
    }
}
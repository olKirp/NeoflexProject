package neostudy.conveyor.service;

import neostudy.conveyor.dto.CreditDTO;
import neostudy.conveyor.dto.EmploymentDTO;
import neostudy.conveyor.dto.ScoringDataDTO;
import neostudy.conveyor.dto.enums.EmploymentStatus;
import neostudy.conveyor.dto.enums.Gender;
import neostudy.conveyor.dto.enums.MaritalStatus;
import neostudy.conveyor.dto.enums.Position;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ScoringServiceImplTest {

    @Autowired
    ScoringServiceImpl scoringService;

    @Autowired
    Constants constants;

    static ScoringDataDTO scoringDataDTO;

    static EmploymentDTO employmentDTO = new EmploymentDTO();

    @BeforeAll
    static void createScoringDataDTO() {
        scoringDataDTO = new ScoringDataDTO();
        employmentDTO = new EmploymentDTO();

        scoringDataDTO.setEmployment(employmentDTO);
    }

    @BeforeEach
    void setDefaultValues() {
        scoringDataDTO.setBirthdate(LocalDate.of(1990, 1, 1));
        scoringDataDTO.setGender(Gender.FEMALE);
        scoringDataDTO.setMaritalStatus(MaritalStatus.SINGLE);
        scoringDataDTO.setDependentAmount(0);

        scoringDataDTO.setAmount(new BigDecimal("100000.00"));
        scoringDataDTO.setTerm(6);
        scoringDataDTO.setIsSalaryClient(false);
        scoringDataDTO.setIsInsuranceEnabled(true);

        employmentDTO.setSalary(new BigDecimal("50000.00"));
        employmentDTO.setEmploymentStatus(EmploymentStatus.EMPLOYEE);
        employmentDTO.setPosition(Position.EMPLOYEE);
        employmentDTO.setWorkExperienceTotal(20);
        employmentDTO.setWorkExperienceCurrent(10);
    }

    @Test
    void givenCorrectScoringData_ThanReturnCreditDTO() {
        CreditDTO creditDTO = scoringService.createCredit(scoringDataDTO);

        assertEquals(scoringDataDTO.getIsInsuranceEnabled(), creditDTO.getIsInsuranceEnabled());
        assertEquals(scoringDataDTO.getIsSalaryClient(), creditDTO.getIsSalaryClient());
        assertEquals(scoringDataDTO.getTerm(), creditDTO.getTerm());
        assertEquals(new BigDecimal("130000.00"), creditDTO.getAmount());
        assertEquals(new BigDecimal("8.30"), creditDTO.getRate());
        assertEquals(new BigDecimal("4.87"), creditDTO.getPsk());
    }

    @Test
    void givenIncorrectAmount_ThanThrowException() {
        String correctMsg = "Requested amount less than " + constants.getMinAmount() + " or bigger than " + constants.getMaxAmount() + " or twenty salaries";

        BigDecimal incorrectAmount = scoringDataDTO.getEmployment().getSalary().multiply(new BigDecimal("20.00")).add(new BigDecimal("1.00"));
        scoringDataDTO.setAmount(incorrectAmount);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> scoringService.createCredit(scoringDataDTO));
        assertEquals(correctMsg, exception.getMessage());

        incorrectAmount = constants.getMinAmount().subtract(BigDecimal.ONE);
        scoringDataDTO.setAmount(incorrectAmount);

        exception = assertThrows(IllegalArgumentException.class, () -> scoringService.createCredit(scoringDataDTO));
        assertEquals(correctMsg, exception.getMessage());

        incorrectAmount = constants.getMaxAmount().add(BigDecimal.ONE);
        scoringDataDTO.setAmount(incorrectAmount);

        exception = assertThrows(IllegalArgumentException.class, () -> scoringService.createCredit(scoringDataDTO));
        assertEquals(correctMsg, exception.getMessage());
    }

    @Test
    void givenIncorrectEmployment_ThanThrowException() {
        String correctMsg = "Loan request is not valid, user is unemployed or has a total work experience less than 12 or current experience less than 3";

        employmentDTO.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> scoringService.createCredit(scoringDataDTO));
        assertEquals(correctMsg, exception.getMessage());

        employmentDTO.setEmploymentStatus(EmploymentStatus.EMPLOYEE);
        employmentDTO.setWorkExperienceCurrent(1);
        exception = assertThrows(IllegalArgumentException.class, () -> scoringService.createCredit(scoringDataDTO));
        assertEquals(correctMsg, exception.getMessage());

        employmentDTO.setWorkExperienceCurrent(24);
        employmentDTO.setWorkExperienceTotal(10);
        exception = assertThrows(IllegalArgumentException.class, () -> scoringService.createCredit(scoringDataDTO));
        assertEquals(correctMsg, exception.getMessage());
    }

    @Test
    void givenNullScoringDataDTO_ThanThrowException() {
        String correctMsg = "ScoringDataDTO is null";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> scoringService.createCredit(null));
        assertEquals(correctMsg, exception.getMessage());
    }

    @Test
    void givenManagerPosition_ThanRateDecrease() {
        employmentDTO.setPosition(Position.MIDDLE_MANAGER);
        BigDecimal rate = scoringService.createCredit(scoringDataDTO).getRate();
        assertEquals(new BigDecimal("6.30"), rate);

        employmentDTO.setPosition(Position.TOP_MANAGER);
        rate = scoringService.createCredit(scoringDataDTO).getRate();
        assertEquals(new BigDecimal("4.30"), rate);
    }

    @Test
    void givenEmploymentStatus_ThanRateIncrease() {
        employmentDTO.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        BigDecimal rate = scoringService.createCredit(scoringDataDTO).getRate();
        assertEquals(new BigDecimal("9.30"), rate);

        employmentDTO.setEmploymentStatus(EmploymentStatus.BUSINESSMAN);
        rate = scoringService.createCredit(scoringDataDTO).getRate();
        assertEquals(new BigDecimal("11.30"), rate);
    }

    @Test
    void givenAgesWithDiscount_ThanRateDecrease() {
        scoringDataDTO.setBirthdate(LocalDate.of(1980, 1, 1));
        scoringDataDTO.setGender(Gender.MALE);

        BigDecimal rate = scoringService.createCredit(scoringDataDTO).getRate();
        assertEquals(new BigDecimal("5.30"), rate);
    }

    @Test
    void givenUnspecifiedGender_ThanRateIncrease() {
        scoringDataDTO.setGender(Gender.UNSPECIFIED);

        BigDecimal rate = scoringService.createCredit(scoringDataDTO).getRate();
        assertEquals(new BigDecimal("11.30"), rate);
    }

    @Test
    void givenFamilyInformation_ThanRateChanges() {
        scoringDataDTO.setMaritalStatus(MaritalStatus.MARRIED);

        BigDecimal rate = scoringService.createCredit(scoringDataDTO).getRate();
        assertEquals(new BigDecimal("5.30"), rate);

        scoringDataDTO.setMaritalStatus(MaritalStatus.DIVORCED);
        rate = scoringService.createCredit(scoringDataDTO).getRate();
        assertEquals(new BigDecimal("9.30"), rate);

        scoringDataDTO.setDependentAmount(1);
        rate = scoringService.createCredit(scoringDataDTO).getRate();
        assertEquals(new BigDecimal("9.30"), rate);

        scoringDataDTO.setDependentAmount(2);
        rate = scoringService.createCredit(scoringDataDTO).getRate();
        assertEquals(new BigDecimal("10.30"), rate);
    }

    @Test
    void givenRateLessThanMin_ThanReturnMinimalRate() {
        BigDecimal minimalRate = constants.getMinimalRate();
        scoringDataDTO.setIsSalaryClient(true);
        scoringDataDTO.setIsInsuranceEnabled(true);
        scoringDataDTO.getEmployment().setPosition(Position.TOP_MANAGER);
        scoringDataDTO.setBirthdate(LocalDate.of(1980, 1, 1));

        assertEquals(minimalRate, scoringService.createCredit(scoringDataDTO).getRate());
    }
}
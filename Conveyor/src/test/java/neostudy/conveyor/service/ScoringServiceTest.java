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

@SpringBootTest
class ScoringServiceTest {

    @Autowired
    ScoringService scoringService;

    static ScoringDataDTO scoringDataDTO;

    static EmploymentDTO employmentDTO = new EmploymentDTO();

    @BeforeAll
    static void createScoringDataDTO() {
        scoringDataDTO = new ScoringDataDTO();
        employmentDTO = new EmploymentDTO();

        employmentDTO.setEmploymentStatus(EmploymentStatus.EMPLOYEE);
        employmentDTO.setPosition(Position.EMPLOYEE);
        employmentDTO.setWorkExperienceTotal(20);
        employmentDTO.setWorkExperienceCurrent(10);
        employmentDTO.setSalary(new BigDecimal("50000.00"));

        scoringDataDTO.setBirthdate(LocalDate.of(1980, 1, 1));
//        scoringDataDTO.setIsInsuranceEnabled(true);
//        scoringDataDTO.setIsSalaryClient(true);
        scoringDataDTO.setEmployment(employmentDTO);
        scoringDataDTO.setAmount(new BigDecimal("100000.00"));
        scoringDataDTO.setTerm(6);

        scoringDataDTO.setMaritalStatus(MaritalStatus.SINGLE);
        scoringDataDTO.setDependentAmount(0);
    }

    @BeforeEach
    void setDefaultValues() {
        scoringDataDTO.setBirthdate(LocalDate.of(1980, 1, 1));
        scoringDataDTO.setAmount(new BigDecimal("100000.00"));
        scoringDataDTO.setTerm(6);
        scoringDataDTO.setBirthdate(LocalDate.of(1980, 1, 1));
        scoringDataDTO.setMaritalStatus(MaritalStatus.SINGLE);
        scoringDataDTO.setDependentAmount(0);
        employmentDTO.setSalary(new BigDecimal("50000.00"));

        employmentDTO.setEmploymentStatus(EmploymentStatus.EMPLOYEE);
        employmentDTO.setPosition(Position.EMPLOYEE);
        employmentDTO.setWorkExperienceTotal(20);
        employmentDTO.setWorkExperienceCurrent(10);
    }

    @Test
    void givenIncorrectAmount_ThanReturnsEmptyCredit() {
        BigDecimal incorrectAmount = scoringDataDTO.getEmployment().getSalary().multiply(new BigDecimal("20.00")).add(new BigDecimal("1.00"));

        scoringDataDTO.setAmount(incorrectAmount);
        assertEquals(new CreditDTO(), scoringService.createCredit(scoringDataDTO));
    }

    @Test
    void givenTopManager_ThanRateDecreaseBy4() {
        employmentDTO.setPosition(Position.TOP_MANAGER);

        BigDecimal rate = scoringService.countRateForEmployment(employmentDTO, new BigDecimal("10"));
        assertEquals(new BigDecimal("6.00"), rate);
    }

    @Test
    void givenMiddleManager_ThanRateDecreaseBy2() {
        employmentDTO.setPosition(Position.MIDDLE_MANAGER);

        BigDecimal rate = scoringService.countRateForEmployment(employmentDTO, new BigDecimal("10"));
        assertEquals(new BigDecimal("8.00"), rate);
    }

    @Test
    void givenSelfEmployed_ThanRateIncreaseBy1() {
        employmentDTO.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);

        BigDecimal rate = scoringService.countRateForEmployment(employmentDTO, new BigDecimal("10"));
        assertEquals(new BigDecimal("11.00"), rate);
    }

    @Test
    void givenBusinessman_ThanRateIncreaseBy3() {
        employmentDTO.setEmploymentStatus(EmploymentStatus.BUSINESSMAN);

        BigDecimal rate = scoringService.countRateForEmployment(employmentDTO, new BigDecimal("10"));
        assertEquals(new BigDecimal("13.00"), rate);
    }

    @Test
    void givenAgesWithDiscount_ThanRateDecreaseBy3() {
        scoringDataDTO.setGender(Gender.FEMALE);
        scoringDataDTO.setBirthdate(LocalDate.of(1975, 12, 12));

        BigDecimal rate = scoringService.countRateForBirthdateAndGender(scoringDataDTO, new BigDecimal("10"));
        assertEquals(new BigDecimal("7.00"), rate);

        scoringDataDTO.setGender(Gender.MALE);

        rate = scoringService.countRateForBirthdateAndGender(scoringDataDTO, new BigDecimal("10"));
        assertEquals(new BigDecimal("7.00"), rate);
    }

    @Test
    void givenUnspecifiedGender_ThanRateIncreaseBy3() {
        scoringDataDTO.setGender(Gender.UNSPECIFIED);
        scoringDataDTO.setBirthdate(LocalDate.of(1975, 12, 12));

        BigDecimal rate = scoringService.countRateForBirthdateAndGender(scoringDataDTO, new BigDecimal("10"));
        assertEquals(new BigDecimal("13.00"), rate);
    }

    @Test
    void givenMarriedUserWithDependents_ThanRateDecreaseBy2() {
        scoringDataDTO.setMaritalStatus(MaritalStatus.MARRIED);
        scoringDataDTO.setDependentAmount(2);

        BigDecimal rate = scoringService.countRateForMaritalStatus(scoringDataDTO, new BigDecimal("10"));
        assertEquals(new BigDecimal("8.00"), rate);
    }
}
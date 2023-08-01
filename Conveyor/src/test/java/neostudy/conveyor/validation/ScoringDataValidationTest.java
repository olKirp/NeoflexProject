package neostudy.conveyor.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import neostudy.conveyor.dto.EmploymentDTO;
import neostudy.conveyor.dto.ScoringDataDTO;
import neostudy.conveyor.dto.enums.Gender;
import neostudy.conveyor.dto.enums.MaritalStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScoringDataValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private static ScoringDataDTO scoringDataDTO;
    private static EmploymentDTO employmentDTO;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();

    }

    @AfterAll
    static void close() {
        validatorFactory.close();
    }

    @BeforeEach
    void createScoringDataDTO () {
        scoringDataDTO = new ScoringDataDTO();
        employmentDTO = new EmploymentDTO();
        employmentDTO.setEmployerINN("123456789012");
        employmentDTO.setSalary(new BigDecimal("50000.00"));


        scoringDataDTO.setFirstName("Name");
        scoringDataDTO.setLastName("Lastname");
        scoringDataDTO.setMiddleName("Middlename");
        scoringDataDTO.setBirthdate(LocalDate.of(1980, 1, 1));
        scoringDataDTO.setIsInsuranceEnabled(true);
        scoringDataDTO.setIsSalaryClient(true);
        scoringDataDTO.setPassportNumber("111111");
        scoringDataDTO.setPassportSeries("1111");
        scoringDataDTO.setPassportIssueDate(LocalDate.of(2000, 1, 1));
        scoringDataDTO.setPassportIssueBranch("PassportIssueBranch");
        scoringDataDTO.setAccount("0123456789");
        scoringDataDTO.setEmployment(employmentDTO);
        scoringDataDTO.setAmount(new BigDecimal("100000.00"));
        scoringDataDTO.setTerm(6);
        scoringDataDTO.setGender(Gender.FEMALE);
        scoringDataDTO.setMaritalStatus(MaritalStatus.SINGLE);
    }


    @ParameterizedTest
    @ValueSource(strings = {"", " ", "aasdfghjklp", "123456789", "12345678901"})
    void givenIncorrectAccount_thenReturnViolations(String account) {
        scoringDataDTO.setAccount(account);
        Set<ConstraintViolation<ScoringDataDTO>> violations = validator.validate(scoringDataDTO);

        assertFalse(violations.isEmpty());
    }


    @Test
    void givenCorrectAccount_thenReturnNoViolations() {
        scoringDataDTO.setAccount("0123456789");
        Set<ConstraintViolation<ScoringDataDTO>> violations = validator.validate(scoringDataDTO);
        violations.forEach(System.out::println);
        assertTrue(violations.isEmpty());
    }

    @Test
    void givenCorrectScoringDataDTO_thenReturnNoViolations() {
        Set<ConstraintViolation<ScoringDataDTO>> violations = validator.validate(scoringDataDTO);

        violations.forEach(System.out::println);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567890123", "qwertyuiopas"})
    @NullAndEmptySource
    void givenIncorrectINN_thenReturnViolations(String INN) {
        employmentDTO.setEmployerINN(INN);
        Set<ConstraintViolation<EmploymentDTO>> violations = validator.validate(employmentDTO);

        assertFalse(violations.isEmpty());
    }

    @Test
    void givenIncorrectSalary_thenReturnViolations() {
        employmentDTO.setSalary(new BigDecimal("-1.00"));
        Set<ConstraintViolation<EmploymentDTO>> violations = validator.validate(employmentDTO);

        assertFalse(violations.isEmpty());
    }

    @Test
    void givenIncorrectExperience_thenReturnViolations() {
        employmentDTO.setWorkExperienceCurrent(-1);
        Set<ConstraintViolation<EmploymentDTO>> violations = validator.validate(employmentDTO);

        assertFalse(violations.isEmpty());
        employmentDTO.setWorkExperienceCurrent(1);
        employmentDTO.setWorkExperienceTotal(-1);
        violations = validator.validate(employmentDTO);
        assertFalse(violations.isEmpty());
    }
}


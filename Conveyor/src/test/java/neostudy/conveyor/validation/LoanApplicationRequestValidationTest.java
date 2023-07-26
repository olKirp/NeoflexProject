package neostudy.conveyor.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import neostudy.conveyor.dto.LoanApplicationRequestDTO;

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

public class LoanApplicationRequestValidationTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private static LoanApplicationRequestDTO request;

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
    void createLoanApplicationRequest() {
        request = new LoanApplicationRequestDTO(new BigDecimal(100000), 10, "Name", "Lastname", "Middlename", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Name", "Na", "MaximumLengthNameNameNameNameE"})
    public void givenCorrectName_thenReturnNoViolations(String name) {
        request.setFirstName(name);
        Set<ConstraintViolation<LoanApplicationRequestDTO>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "a", "LongerThenMaximumLengthNameName", "Вася", "Name1", "Name!", "name"})
    @NullAndEmptySource
    public void givenIncorrectName_thenReturnViolation(String name) {
        request.setFirstName(name);
        Set<ConstraintViolation<LoanApplicationRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void givenCorrectPassportNumber_thenReturnNoViolation() {
        request.setPassportNumber("123456");
        Set<ConstraintViolation<LoanApplicationRequestDTO>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abcdef", "12345", "1234567"})
    @NullAndEmptySource
    public void givenIncorrectPassportNumber_thenReturnViolation(String passportNumber) {
        request.setPassportNumber(passportNumber);
        Set<ConstraintViolation<LoanApplicationRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void givenCorrectPassportSeries_thenReturnNoViolation() {
        request.setPassportSeries("1234");
        Set<ConstraintViolation<LoanApplicationRequestDTO>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abcd", "123", "12345"})
    @NullAndEmptySource
    public void givenIncorrectPassportSeries_thenReturnViolation(String passportSeries) {
        request.setPassportSeries(passportSeries);
        Set<ConstraintViolation<LoanApplicationRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abcd", "abcd@", "abcd@gg", "abcd@gg."})
    @NullAndEmptySource
    public void givenIncorrectEmail_thenReturnViolation(String email) {
        request.setEmail(email);
        Set<ConstraintViolation<LoanApplicationRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"TestMail@mail.co.in", "Test-Mail_mail@mail-mail.ru"})//@mail.ru
    public void givenCorrectEmail_thenReturnNoViolation(String email) {
        request.setEmail(email);
        Set<ConstraintViolation<LoanApplicationRequestDTO>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}

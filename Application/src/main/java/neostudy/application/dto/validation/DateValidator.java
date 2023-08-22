package neostudy.application.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateValidator implements
        ConstraintValidator<PastConstraint, LocalDate> {

    @Override
    public void initialize(PastConstraint contactNumber) {
    }

    @Override
    public boolean isValid(LocalDate contactField, ConstraintValidatorContext cxt) {
        return contactField != null && contactField.isBefore(LocalDate.now());
    }
}
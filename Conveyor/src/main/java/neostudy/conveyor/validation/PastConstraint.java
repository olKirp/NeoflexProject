package neostudy.conveyor.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PastConstraint {
    String message() default "Date should be before today";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

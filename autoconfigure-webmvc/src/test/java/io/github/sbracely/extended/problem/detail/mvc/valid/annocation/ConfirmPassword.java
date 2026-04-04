package io.github.sbracely.extended.problem.detail.mvc.valid.annocation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import io.github.sbracely.extended.problem.detail.mvc.valid.validator.ConfirmPasswordValidator;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConfirmPasswordValidator.class)
public @interface ConfirmPassword {
    String message() default "Confirm password error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields() default {};
}

package io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation;

import io.github.sbracely.extended.problem.detail.webflux.example.valid.validator.Boot3FluxConfirmPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Boot3FluxConfirmPasswordValidator.class)
public @interface Boot3FluxConfirmPassword {
    String message() default "Confirm password error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields() default {};
}

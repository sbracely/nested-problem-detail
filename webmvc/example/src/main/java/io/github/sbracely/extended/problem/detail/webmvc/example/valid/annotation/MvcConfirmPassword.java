package io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation;

import io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator.MvcConfirmPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MvcConfirmPasswordValidator.class)
public @interface MvcConfirmPassword {
    String message() default "{mvc.example.validation.confirm-password}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields() default {};
}

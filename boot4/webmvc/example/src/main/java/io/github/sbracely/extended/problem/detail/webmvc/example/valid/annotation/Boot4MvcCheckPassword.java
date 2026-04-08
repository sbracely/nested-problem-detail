package io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation;

import io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator.Boot4MvcCheckPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Boot4MvcCheckPasswordValidator.class)
public @interface Boot4MvcCheckPassword {
    String message() default "Check password error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

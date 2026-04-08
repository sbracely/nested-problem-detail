package io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation;

import io.github.sbracely.extended.problem.detail.webflux.example.valid.validator.Boot4FluxCheckPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Boot4FluxCheckPasswordValidator.class)
public @interface Boot4FluxCheckPassword {
    String message() default "Check password error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

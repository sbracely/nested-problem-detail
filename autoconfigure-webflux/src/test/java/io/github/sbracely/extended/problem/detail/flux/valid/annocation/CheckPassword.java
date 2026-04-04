package io.github.sbracely.extended.problem.detail.flux.valid.annocation;

import io.github.sbracely.extended.problem.detail.flux.valid.validator.CheckPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckPasswordValidator.class)
public @interface CheckPassword {
    String message() default "Check password error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

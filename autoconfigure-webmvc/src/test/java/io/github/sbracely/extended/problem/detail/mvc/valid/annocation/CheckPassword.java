package io.github.sbracely.extended.problem.detail.mvc.valid.annocation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import io.github.sbracely.extended.problem.detail.mvc.valid.validator.CheckPasswordValidator;

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

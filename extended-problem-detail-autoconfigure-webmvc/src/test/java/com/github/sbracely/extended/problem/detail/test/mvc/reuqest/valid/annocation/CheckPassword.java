package com.github.sbracely.extended.problem.detail.test.mvc.reuqest.valid.annocation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.valid.validator.CheckPasswordValidator;

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

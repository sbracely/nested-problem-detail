package io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation;


import io.github.sbracely.extended.problem.detail.webflux.example.valid.validator.CheckNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckNameValidator.class)
public @interface CheckName {
    String message() default "Name is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int nameIndex();

    int problemDetailRequestIndex();
}

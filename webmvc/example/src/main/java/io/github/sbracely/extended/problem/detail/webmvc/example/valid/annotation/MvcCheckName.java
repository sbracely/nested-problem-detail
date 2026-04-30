package io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation;


import io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator.MvcCheckNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MvcCheckNameValidator.class)
public @interface MvcCheckName {
    String message() default "{mvc.example.validation.name.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int nameIndex();

    int problemDetailRequestIndex();
}

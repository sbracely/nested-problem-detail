package io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation;


import io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator.Boot4MvcCheckNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Boot4MvcCheckNameValidator.class)
public @interface Boot4MvcCheckName {
    String message() default "Name is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int nameIndex();

    int problemDetailRequestIndex();
}

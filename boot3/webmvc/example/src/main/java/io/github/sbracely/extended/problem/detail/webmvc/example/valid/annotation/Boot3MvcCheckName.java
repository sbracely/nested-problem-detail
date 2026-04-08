package io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation;


import io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator.Boot3MvcCheckNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Boot3MvcCheckNameValidator.class)
public @interface Boot3MvcCheckName {
    String message() default "Name is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int nameIndex();

    int problemDetailRequestIndex();
}

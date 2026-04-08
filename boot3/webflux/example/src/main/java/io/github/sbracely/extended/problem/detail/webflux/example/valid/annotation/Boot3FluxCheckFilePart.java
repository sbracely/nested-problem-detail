package io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation;

import io.github.sbracely.extended.problem.detail.webflux.example.valid.validator.Boot3FluxCheckFilePartValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Boot3FluxCheckFilePartValidator.class)
public @interface Boot3FluxCheckFilePart {
    String message() default "Check file error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] extensionInclude() default {};

    String extensionIncludeMessage() default "Extension not support";

    boolean required() default true;

    String requiredMessage() default "File required";
}

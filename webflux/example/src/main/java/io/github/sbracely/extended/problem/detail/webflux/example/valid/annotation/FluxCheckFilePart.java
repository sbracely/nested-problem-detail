package io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation;

import io.github.sbracely.extended.problem.detail.webflux.example.valid.validator.FluxCheckFilePartValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FluxCheckFilePartValidator.class)
public @interface FluxCheckFilePart {
    String message() default "{flux.example.upload.file.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] extensionInclude() default {};

    String extensionIncludeMessage() default "{flux.example.upload.file.unsupported-extension}";

    boolean required() default true;

    String requiredMessage() default "{flux.example.upload.file.required}";
}

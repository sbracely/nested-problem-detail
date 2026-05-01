package io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation;


import io.github.sbracely.extended.problem.detail.webflux.example.valid.validator.FluxCheckNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FluxCheckNameValidator.class)
public @interface FluxCheckName {
    String message() default "{flux.example.request.name.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int nameIndex();

    int problemDetailRequestIndex();
}

package io.github.sbracely.extend.problem.detail.webmvc.example.valid.annocation;

import io.github.sbracely.extend.problem.detail.webmvc.example.valid.validator.CheckMultipartFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckMultipartFileValidator.class)
public @interface CheckMultipartFile {
    String message() default "Check file error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] extensionInclude() default {};

    String extensionIncludeMessage() default "Extension not support";

    boolean required() default true;

    String requiredMessage() default "File required";
}

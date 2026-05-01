package io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation;

import io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator.MvcCheckMultipartFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MvcCheckMultipartFileValidator.class)
public @interface MvcCheckMultipartFile {
    String message() default "{mvc.example.upload.file.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] extensionInclude() default {};

    String extensionIncludeMessage() default "{mvc.example.upload.file.unsupported-extension}";

    boolean required() default true;

    String requiredMessage() default "{mvc.example.upload.file.required}";
}

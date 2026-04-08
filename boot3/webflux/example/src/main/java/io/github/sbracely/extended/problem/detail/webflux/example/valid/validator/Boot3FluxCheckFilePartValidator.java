package io.github.sbracely.extended.problem.detail.webflux.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.Boot3FluxCheckFilePart;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.codec.multipart.FilePart;

import java.util.Arrays;

public class Boot3FluxCheckFilePartValidator implements ConstraintValidator<Boot3FluxCheckFilePart, FilePart> {

    private boolean required;
    private String requiredMessage;
    private String[] extensions;
    private String extensionsMessage;

    @Override
    public void initialize(Boot3FluxCheckFilePart constraintAnnotation) {
        required = constraintAnnotation.required();
        extensions = constraintAnnotation.extensionInclude();
        requiredMessage = constraintAnnotation.requiredMessage();
        extensionsMessage = constraintAnnotation.extensionIncludeMessage();
    }

    @Override
    public boolean isValid(FilePart filePart, ConstraintValidatorContext context) {
        boolean valid = required(filePart);
        if (!valid) {
            return validFalse(context, requiredMessage);
        }
        valid = extensions(filePart);
        if (valid) {
            return true;
        }
        return validFalse(context, extensionsMessage);
    }

    private boolean validFalse(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("file")
                .addConstraintViolation();
        return false;
    }

    private boolean required(FilePart filePart) {
        if (!required) {
            return true;
        }
        return null != filePart;
    }

    private boolean extensions(FilePart filePart) {
        if (null == filePart) {
            return false;
        }
        if (null == extensions || extensions.length == 0) {
            return true;
        }
        String originalFilename = filePart.filename();
        if (StringUtils.isBlank(originalFilename)) {
            return false;
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        return Arrays.asList(extensions).contains(extension);
    }
}

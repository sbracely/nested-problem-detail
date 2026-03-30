package com.github.sbracely.extended.problem.detail.test.mvc.reuqest.valid.validator;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ArrayUtils;
import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.valid.annocation.CheckMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class CheckMultipartFileValidator implements ConstraintValidator<CheckMultipartFile, MultipartFile> {

    private boolean required;
    private String requiredMessage;
    private String[] extensions;
    private String extensionsMessage;

    @Override
    public void initialize(CheckMultipartFile constraintAnnotation) {
        required = constraintAnnotation.required();
        extensions = constraintAnnotation.extensionInclude();
        requiredMessage = constraintAnnotation.requiredMessage();
        extensionsMessage = constraintAnnotation.extensionIncludeMessage();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        boolean valid = required(multipartFile);
        if (!valid) {
            return validFalse(context, requiredMessage);
        }
        valid = extensions(multipartFile);
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

    private boolean required(MultipartFile multipartFile) {
        if (required) {
            return null != multipartFile && !multipartFile.isEmpty();
        }
        return true;
    }

    private boolean extensions(MultipartFile multipartFile) {
        if (null == multipartFile) {
            return false;
        }
        if (ArrayUtils.isEmpty(extensions)) {
            return true;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            return false;
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        return Arrays.asList(extensions).contains(extension);
    }
}

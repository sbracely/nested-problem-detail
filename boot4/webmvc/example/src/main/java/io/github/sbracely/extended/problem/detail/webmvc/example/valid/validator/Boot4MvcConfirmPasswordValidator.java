package io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webmvc.example.request.Boot4MvcProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.Boot4MvcConfirmPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class Boot4MvcConfirmPasswordValidator implements ConstraintValidator<Boot4MvcConfirmPassword, Boot4MvcProblemDetailRequest> {

    private String[] fields;

    @Override
    public void initialize(Boot4MvcConfirmPassword constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Boot4MvcProblemDetailRequest problemDetailRequest, ConstraintValidatorContext context) {
        boolean valid = Objects.equals(problemDetailRequest.getPassword(), problemDetailRequest.getConfirmPassword());
        if (valid) {
            return true;
        }
        if (null == fields || fields.length == 0) {
            return false;
        }
        context.disableDefaultConstraintViolation();
        String defaultConstraintMessageTemplate = context.getDefaultConstraintMessageTemplate();
        for (String field : fields) {
            context.buildConstraintViolationWithTemplate(defaultConstraintMessageTemplate)
                    .addPropertyNode(field)
                    .addConstraintViolation();
        }
        return false;
    }
}
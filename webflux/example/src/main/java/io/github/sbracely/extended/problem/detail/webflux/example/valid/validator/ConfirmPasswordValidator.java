package io.github.sbracely.extended.problem.detail.webflux.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webflux.example.request.ProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.ConfirmPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class ConfirmPasswordValidator implements ConstraintValidator<ConfirmPassword, ProblemDetailRequest> {

    private String[] fields;

    @Override
    public void initialize(ConfirmPassword constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(ProblemDetailRequest problemDetailRequest, ConstraintValidatorContext context) {
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
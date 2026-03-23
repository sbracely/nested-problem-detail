package org.example.exceptionhandlerexample.reuqest.valid.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Strings;
import org.example.exceptionhandlerexample.reuqest.problem.detail.ProblemDetailRequest;
import org.example.exceptionhandlerexample.reuqest.valid.annocation.ConfirmPassword;

public class ConfirmPasswordValidator implements ConstraintValidator<ConfirmPassword, ProblemDetailRequest> {

    private String[] fields;

    @Override
    public void initialize(ConfirmPassword constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(ProblemDetailRequest problemDetailRequest, ConstraintValidatorContext context) {
        boolean valid = Strings.CS.equals(problemDetailRequest.getPassword(), problemDetailRequest.getConfirmPassword());
        if (valid) {
            return true;
        }
        if (ArrayUtils.isEmpty(fields)) {
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
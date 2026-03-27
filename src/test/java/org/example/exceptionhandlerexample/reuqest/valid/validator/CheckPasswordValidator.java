package org.example.exceptionhandlerexample.reuqest.valid.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.example.exceptionhandlerexample.reuqest.ProblemDetailRequest;
import org.example.exceptionhandlerexample.reuqest.valid.annocation.CheckPassword;

import java.util.Optional;

public class CheckPasswordValidator implements ConstraintValidator<CheckPassword, ProblemDetailRequest> {

    @Override
    public boolean isValid(ProblemDetailRequest problemDetailRequest, ConstraintValidatorContext context) {
        boolean valid = Optional.ofNullable(problemDetailRequest)
                .map(ProblemDetailRequest::getPassword)
                .map(StringUtils::isNotBlank)
                .orElse(false);
        if (valid) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("password")
                .addConstraintViolation();
        return false;
    }
}

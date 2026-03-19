package org.example.exceptionhandlerexample.reuqest.valid.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.Strings;
import org.example.exceptionhandlerexample.reuqest.problem.ProblemRequest;
import org.example.exceptionhandlerexample.reuqest.valid.annocation.ConfirmPassword;

public class ConfirmPasswordValidator implements ConstraintValidator<ConfirmPassword, ProblemRequest> {
    @Override
    public boolean isValid(ProblemRequest problemRequest, ConstraintValidatorContext context) {
        return Strings.CS.equals(problemRequest.getPassword(), problemRequest.getConfirmPassword());
    }
}
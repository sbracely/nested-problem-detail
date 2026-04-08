package io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webmvc.example.request.Boot3MvcProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.Boot3MvcCheckPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class Boot3MvcCheckPasswordValidator implements ConstraintValidator<Boot3MvcCheckPassword, Boot3MvcProblemDetailRequest> {

    @Override
    public boolean isValid(Boot3MvcProblemDetailRequest problemDetailRequest, ConstraintValidatorContext context) {
        boolean valid = Optional.ofNullable(problemDetailRequest)
                .map(Boot3MvcProblemDetailRequest::getPassword)
                .map(password -> !password.isBlank())
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

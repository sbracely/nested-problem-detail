package io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webmvc.example.request.Boot4MvcProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.Boot4MvcCheckPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class Boot4MvcCheckPasswordValidator implements ConstraintValidator<Boot4MvcCheckPassword, Boot4MvcProblemDetailRequest> {

    @Override
    public boolean isValid(Boot4MvcProblemDetailRequest problemDetailRequest, ConstraintValidatorContext context) {
        boolean valid = Optional.ofNullable(problemDetailRequest)
                .map(Boot4MvcProblemDetailRequest::getPassword)
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

package io.github.sbracely.extended.problem.detail.webflux.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webflux.example.request.Boot4FluxProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.Boot4FluxCheckPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class Boot4FluxCheckPasswordValidator implements ConstraintValidator<Boot4FluxCheckPassword, Boot4FluxProblemDetailRequest> {

    @Override
    public boolean isValid(Boot4FluxProblemDetailRequest problemDetailRequest, ConstraintValidatorContext context) {
        boolean valid = Optional.ofNullable(problemDetailRequest)
                .map(Boot4FluxProblemDetailRequest::getPassword)
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

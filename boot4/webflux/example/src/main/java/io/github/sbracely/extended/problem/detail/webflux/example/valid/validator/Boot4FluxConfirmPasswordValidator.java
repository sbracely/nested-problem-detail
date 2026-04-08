package io.github.sbracely.extended.problem.detail.webflux.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webflux.example.request.Boot4FluxProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.Boot4FluxConfirmPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class Boot4FluxConfirmPasswordValidator implements ConstraintValidator<Boot4FluxConfirmPassword, Boot4FluxProblemDetailRequest> {

    private String[] fields;

    @Override
    public void initialize(Boot4FluxConfirmPassword constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Boot4FluxProblemDetailRequest problemDetailRequest, ConstraintValidatorContext context) {
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
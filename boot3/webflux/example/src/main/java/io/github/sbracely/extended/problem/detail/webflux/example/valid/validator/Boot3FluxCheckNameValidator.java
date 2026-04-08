package io.github.sbracely.extended.problem.detail.webflux.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webflux.example.request.Boot3FluxProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.Boot3FluxCheckName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

import java.util.Objects;
import java.util.Optional;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class Boot3FluxCheckNameValidator implements ConstraintValidator<Boot3FluxCheckName, Object[]> {
    private int nameIndex;
    private int problemDetailRequestIndex;

    @Override
    public void initialize(Boot3FluxCheckName constraintAnnotation) {
        nameIndex = constraintAnnotation.nameIndex();
        problemDetailRequestIndex = constraintAnnotation.problemDetailRequestIndex();
    }

    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        if (value == null || value.length <= problemDetailRequestIndex) {
            return false;
        }
        String name = (String) value[nameIndex];
        String problemDetailRequestName = Optional.ofNullable(value[problemDetailRequestIndex])
                .map(obj -> ((Boot3FluxProblemDetailRequest) obj))
                .map(Boot3FluxProblemDetailRequest::getName)
                .orElse(null);
        return Objects.equals(name, problemDetailRequestName);
    }
}

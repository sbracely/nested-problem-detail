package io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webmvc.example.request.Boot4MvcProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.Boot4MvcCheckName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

import java.util.Objects;
import java.util.Optional;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class Boot4MvcCheckNameValidator implements ConstraintValidator<Boot4MvcCheckName, Object[]> {
    private int nameIndex;
    private int problemDetailRequestIndex;

    @Override
    public void initialize(Boot4MvcCheckName constraintAnnotation) {
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
                .map(obj -> ((Boot4MvcProblemDetailRequest) obj))
                .map(Boot4MvcProblemDetailRequest::getName)
                .orElse(null);
        return Objects.equals(name, problemDetailRequestName);
    }
}

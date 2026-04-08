package io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webmvc.example.request.Boot3MvcProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.Boot3MvcCheckName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

import java.util.Objects;
import java.util.Optional;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class Boot3MvcCheckNameValidator implements ConstraintValidator<Boot3MvcCheckName, Object[]> {
    private int nameIndex;
    private int problemDetailRequestIndex;

    @Override
    public void initialize(Boot3MvcCheckName constraintAnnotation) {
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
                .map(obj -> ((Boot3MvcProblemDetailRequest) obj))
                .map(Boot3MvcProblemDetailRequest::getName)
                .orElse(null);
        return Objects.equals(name, problemDetailRequestName);
    }
}

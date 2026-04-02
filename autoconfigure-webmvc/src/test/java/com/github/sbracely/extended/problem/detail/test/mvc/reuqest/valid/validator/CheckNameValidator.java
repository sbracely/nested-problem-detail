package com.github.sbracely.extended.problem.detail.test.mvc.reuqest.valid.validator;

import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.ProblemDetailRequest;
import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.valid.annocation.CheckName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

import java.util.Objects;
import java.util.Optional;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class CheckNameValidator implements ConstraintValidator<CheckName, Object[]> {
    private int nameIndex;
    private int problemDetailRequestIndex;

    @Override
    public void initialize(CheckName constraintAnnotation) {
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
                .map(obj -> ((ProblemDetailRequest) obj))
                .map(ProblemDetailRequest::getName)
                .orElse(null);
        return Objects.equals(name, problemDetailRequestName);
    }
}

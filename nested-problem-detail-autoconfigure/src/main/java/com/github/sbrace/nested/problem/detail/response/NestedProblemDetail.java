package com.github.sbrace.nested.problem.detail.response;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ProblemDetail;

import java.util.List;

public class NestedProblemDetail extends ProblemDetail {

    @Nullable
    private String errorCode;

    @Nullable
    private List<Error> errors;

    public NestedProblemDetail() {
    }

    public NestedProblemDetail(ProblemDetail problemDetail) {
        super(problemDetail);
        this.errorCode = ErrorCode.httpStatusValue(problemDetail.getStatus());
    }

    public @Nullable String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(@Nullable String errorCode) {
        this.errorCode = errorCode;
    }

    public @Nullable List<Error> getErrors() {
        return errors;
    }

    public void setErrors(@Nullable List<Error> errors) {
        this.errors = errors;
    }

    @Override
    protected String initToStringContent() {
        return super.initToStringContent() +
                ", errorCode='" + errorCode + "'" +
                ", errors=" + errors;
    }
}

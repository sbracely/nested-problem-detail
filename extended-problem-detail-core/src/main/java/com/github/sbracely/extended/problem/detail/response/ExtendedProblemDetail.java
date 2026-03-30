package com.github.sbracely.extended.problem.detail.response;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ProblemDetail;

import java.util.ArrayList;
import java.util.List;

public class ExtendedProblemDetail extends ProblemDetail {

    @Nullable
    private List<Error> errors;

    public ExtendedProblemDetail() {
    }

    public ExtendedProblemDetail(ProblemDetail problemDetail) {
        super(problemDetail);
        if (problemDetail instanceof ExtendedProblemDetail extendedProblemDetail) {
            if (extendedProblemDetail.errors != null) {
                this.errors = new ArrayList<>(extendedProblemDetail.errors);
            }
        }
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
                ", errors=" + errors;
    }
}

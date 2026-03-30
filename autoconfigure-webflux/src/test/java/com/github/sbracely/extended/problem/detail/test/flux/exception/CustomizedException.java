package com.github.sbracely.extended.problem.detail.test.flux.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class CustomizedException extends ErrorResponseException {

    public CustomizedException(HttpStatusCode status, ProblemDetail body) {
        super(status, body, null);
    }

    public CustomizedException(HttpStatusCode status, ProblemDetail body, Throwable cause) {
        super(status, body, cause);
    }
}

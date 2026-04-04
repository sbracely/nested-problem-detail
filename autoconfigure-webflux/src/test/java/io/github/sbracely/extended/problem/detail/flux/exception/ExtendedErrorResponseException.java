package io.github.sbracely.extended.problem.detail.flux.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ExtendedErrorResponseException extends ErrorResponseException {

    public ExtendedErrorResponseException(HttpStatusCode status, ProblemDetail body) {
        super(status, body, null);
    }

    public ExtendedErrorResponseException(HttpStatusCode status, ProblemDetail body, Throwable cause) {
        super(status, body, cause);
    }
}

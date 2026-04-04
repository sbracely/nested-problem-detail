package io.github.sbracely.extended.problem.detail.mvc.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ExtendedErrorResponseException extends ErrorResponseException {

    public ExtendedErrorResponseException(HttpStatusCode status, ProblemDetail body) {
        super(status, body, null);
    }
}

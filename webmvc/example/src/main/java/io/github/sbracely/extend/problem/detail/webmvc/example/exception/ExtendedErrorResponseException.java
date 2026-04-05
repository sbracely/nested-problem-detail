package io.github.sbracely.extend.problem.detail.webmvc.example.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ExtendedErrorResponseException extends ErrorResponseException {

    public ExtendedErrorResponseException(HttpStatusCode status, ProblemDetail body) {
        super(status, body, null);
    }
}

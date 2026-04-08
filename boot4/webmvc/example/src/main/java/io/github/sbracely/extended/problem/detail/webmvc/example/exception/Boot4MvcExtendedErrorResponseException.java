package io.github.sbracely.extended.problem.detail.webmvc.example.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class Boot4MvcExtendedErrorResponseException extends ErrorResponseException {

    public Boot4MvcExtendedErrorResponseException(HttpStatusCode status, ProblemDetail body) {
        super(status, body, null);
    }
}

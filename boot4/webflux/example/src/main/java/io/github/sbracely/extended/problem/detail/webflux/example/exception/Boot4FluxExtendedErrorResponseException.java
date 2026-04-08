package io.github.sbracely.extended.problem.detail.webflux.example.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class Boot4FluxExtendedErrorResponseException extends ErrorResponseException {

    public Boot4FluxExtendedErrorResponseException(HttpStatusCode status, ProblemDetail body) {
        super(status, body, null);
    }

}

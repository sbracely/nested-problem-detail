package org.example.exceptionhandlerexample.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ProblemDetail;

import java.util.List;

@Getter
@Setter
public class ProblemDetails extends ProblemDetail {

    private List<Object> errors;

    public ProblemDetails() {
    }

    public ProblemDetails(ProblemDetail problemDetail) {
        super(problemDetail);
        errors = List.of(problemDetail);
    }

}

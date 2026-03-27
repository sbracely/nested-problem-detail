package org.example.exceptionhandlerexample.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ProblemDetailService {

    public String createProblemDetail(@NotBlank(message = "message must not be blank") String message) {
        return message;
    }
}

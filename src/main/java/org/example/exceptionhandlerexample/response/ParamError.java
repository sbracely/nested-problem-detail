package org.example.exceptionhandlerexample.response;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Slf4j
@Getter
@Setter
public class ParamError {
    private String field;
    private String message;

    public ParamError(ObjectError objectError) {
        if (objectError instanceof FieldError fieldError) {
            this.field = fieldError.getField();
        }
        this.message = objectError.getDefaultMessage();
    }

    public ParamError(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
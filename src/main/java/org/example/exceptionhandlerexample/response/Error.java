package org.example.exceptionhandlerexample.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Error {
    private Type type;
    private String field;
    private String message;

    public Error(ObjectError objectError) {
        if (objectError instanceof FieldError fieldError) {
            this.field = fieldError.getField();
        }
        this.message = objectError.getDefaultMessage();
        this.type = Type.PARAMETER;
    }

    public Error(String field, String message, Type type) {
        this.field = field;
        this.message = message;
        this.type = type;
    }

    public enum Type {
        PARAMETER,
        COOKIE,
        HEADER,
    }
}
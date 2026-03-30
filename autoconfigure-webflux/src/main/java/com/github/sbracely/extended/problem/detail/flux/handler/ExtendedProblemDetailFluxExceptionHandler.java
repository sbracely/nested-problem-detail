package com.github.sbracely.extended.problem.detail.flux.handler;

import com.github.sbracely.extended.problem.detail.response.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExtendedProblemDetailFluxExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExtendedProblemDetailFluxExceptionHandler.class);


    private Error ObjectErrorConvertToError(ObjectError objectError) {
        Error error = new Error();
        if (objectError instanceof FieldError fieldError) {
            error.setField(fieldError.getField());
        }
        error.setMessage(objectError.getDefaultMessage());
        error.setType(Error.Type.PARAMETER);
        return error;
    }
}

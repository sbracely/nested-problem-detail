package org.example.exceptionhandlerexample.component;

import org.example.exceptionhandlerexample.response.ProblemDetails;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ProblemDetail createProblemDetail(Exception ex, HttpStatusCode status, String defaultDetail, @Nullable String detailMessageCode, Object @Nullable [] detailMessageArguments, WebRequest request) {
        ProblemDetail problemDetail = super.createProblemDetail(ex, status, defaultDetail, detailMessageCode, detailMessageArguments, request);
        return new ProblemDetails(problemDetail);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Object[] args = new Object[]{ex.getPropertyName(), ex.getValue()};
        String var10000 = String.valueOf(args[0]);
        String defaultDetail = "Failed to convert '" + var10000 + "' with value: '" + args[1] + "'";
        ProblemDetail body = this.createProblemDetail(ex, status, defaultDetail, (String) null, args, request);
        return this.handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Object[] args = new Object[]{ex.getPropertyName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : ""};
        String var10000 = String.valueOf(args[0]);
        String defaultDetail = "Failed to convert '" + var10000 + "' with value: '" + args[1] + "'";
        String messageCode = ErrorResponse.getDefaultDetailMessageCode(TypeMismatchException.class, null);
        ProblemDetail body = this.createProblemDetail(ex, status, defaultDetail, messageCode, args, request);
        return this.handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodValidationException(MethodValidationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ProblemDetail body = this.createProblemDetail(ex, status, "Validation failed", null, null, request);
        return this.handleExceptionInternal(ex, body, headers, status, request);
    }

}

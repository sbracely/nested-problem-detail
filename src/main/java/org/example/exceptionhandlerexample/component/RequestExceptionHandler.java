package org.example.exceptionhandlerexample.component;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptionhandlerexample.response.ParamError;
import org.example.exceptionhandlerexample.response.ProblemDetails;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ParamError> paramErrorList = ex.getBindingResult().getAllErrors().stream().map(ParamError::new).toList();
        ProblemDetails problemDetails = new ProblemDetails(ex.getBody());
        problemDetails.setErrors(paramErrorList);
        return handleExceptionInternal(ex, problemDetails, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ParamError> paramErrorList = new ArrayList<>();
        ex.visitResults(new HandlerMethodValidationException.Visitor() {

            @Override
            public void cookieValue(CookieValue cookieValue, ParameterValidationResult result) {
                log.info("result = {}", result);
            }

            @Override
            public void matrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result) {
                log.info("result = {}", result);
            }

            @Override
            public void modelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors) {
                log.info("errors = {}", errors);
            }

            @Override
            public void pathVariable(PathVariable pathVariable, ParameterValidationResult result) {
                log.info("result = {}", result);
            }

            @Override
            public void requestBody(RequestBody requestBody, ParameterErrors errors) {
                log.info("errors = {}", errors);
                errors.getAllErrors().stream().map(ParamError::new).forEach(paramErrorList::add);
            }

            @Override
            public void requestHeader(RequestHeader requestHeader, ParameterValidationResult result) {
                log.info("result = {}", result);
            }

            @Override
            public void requestParam(@Nullable RequestParam requestParam, ParameterValidationResult result) {
                log.info("result = {}", result);
                String parameterName = result.getMethodParameter().getParameterName();
                result.getResolvableErrors().stream().map(MessageSourceResolvable::getDefaultMessage)
                        .map(defaultMessage -> new ParamError(parameterName, defaultMessage))
                        .forEach(paramErrorList::add);
            }

            @Override
            public void requestPart(RequestPart requestPart, ParameterErrors errors) {
                log.info("errors = {}", errors);
            }

            @Override
            public void other(ParameterValidationResult result) {
                log.info("result = {}", result);
            }

            @Override
            public void requestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result) {
                log.info("result = {}", result);
                HandlerMethodValidationException.Visitor.super.requestBodyValidationResult(requestBody, result);
            }
        });
        ProblemDetails problemDetails = new ProblemDetails(ex.getBody());
        problemDetails.setErrors(paramErrorList);
        return handleExceptionInternal(ex, problemDetails, headers, status, request);
    }
}

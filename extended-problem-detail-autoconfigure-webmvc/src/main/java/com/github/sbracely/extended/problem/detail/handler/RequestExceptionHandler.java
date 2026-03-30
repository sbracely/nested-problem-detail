package com.github.sbracely.extended.problem.detail.handler;

import com.github.sbracely.extended.problem.detail.response.Error;
import com.github.sbracely.extended.problem.detail.response.ExtendedProblemDetail;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestExceptionHandler.class);

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<Error> errors = ex.getBindingResult().getAllErrors().stream().map(this::ObjectErrorConvertToError).toList();
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(ex.getBody());
        extendedProblemDetail.setErrors(errors);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
    }

    @Override
    public @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<Error> errorList = new ArrayList<>();
        ex.visitResults(new HandlerMethodValidationException.Visitor() {

            @Override
            public void cookieValue(CookieValue cookieValue, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.COOKIE, getParameterName(result));
            }

            @Override
            public void matrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.PARAMETER, getParameterName(result));
            }

            @Override
            public void modelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors) {
                processParameterErrors(errors);
            }

            @Override
            public void pathVariable(PathVariable pathVariable, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.PARAMETER, getParameterName(result));
            }

            @Override
            public void requestBody(RequestBody requestBody, ParameterErrors errors) {
                processParameterErrors(errors);
            }

            @Override
            public void requestHeader(RequestHeader requestHeader, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.HEADER, getParameterName(result));
            }

            @Override
            public void requestParam(@Nullable RequestParam requestParam, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.PARAMETER, getParameterName(result));
            }

            @Override
            public void requestPart(RequestPart requestPart, ParameterErrors errors) {
                processParameterErrors(errors);
            }

            @Override
            public void other(ParameterValidationResult result) {
                result.getResolvableErrors().forEach(error ->
                        log.error("codes: {}, defaultMessage: {}", error.getCodes(), error.getDefaultMessage()));
            }

            @Override
            public void requestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.PARAMETER, null);
            }

            private @Nullable String getParameterName(ParameterValidationResult result) {
                return result.getMethodParameter().getParameterName();
            }

            private void processParameterValidationResult(ParameterValidationResult result,
                                                          Error.Type errorType,
                                                          @Nullable String parameterName) {
                result.getResolvableErrors().stream().map(MessageSourceResolvable::getDefaultMessage)
                        .map(defaultMessage -> new Error(errorType, parameterName, defaultMessage))
                        .forEach(errorList::add);
            }

            private void processParameterErrors(ParameterErrors errors) {
                errors.getAllErrors().stream().map(RequestExceptionHandler.this::ObjectErrorConvertToError).forEach(errorList::add);
            }
        });
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(ex.getBody());
        extendedProblemDetail.setErrors(errorList);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleErrorResponseException(
            ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex instanceof WebExchangeBindException exchangeBindException) {
            ProblemDetail body = exchangeBindException.getBody();
            BindingResult bindingResult = exchangeBindException.getBindingResult();
            List<Error> errors = bindingResult.getAllErrors().stream().map(this::ObjectErrorConvertToError).toList();
            ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(body);
            extendedProblemDetail.setErrors(errors);
            return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
        }
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleAsyncRequestNotUsableException(
            AsyncRequestNotUsableException ex, WebRequest request) {
        log.error("handleAsyncRequestNotUsableException", ex);
        return null;
    }

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

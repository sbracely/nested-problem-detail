package com.github.sbracely.extended.problem.detail.flux.handler;

import com.github.sbracely.extended.problem.detail.core.logging.ExtendedProblemDetailLog;
import com.github.sbracely.extended.problem.detail.core.response.Error;
import com.github.sbracely.extended.problem.detail.core.response.ExtendedProblemDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * WebFlux Extended Problem Detail Exception Handler.
 * <p>
 * This exception handler extends Spring WebFlux's {@link ResponseEntityExceptionHandler} to provide
 * enhanced validation error handling for reactive web applications. It intercepts validation
 * exceptions and converts them into extended problem detail responses with field-level error information.
 * </p>
 * <p>
 * This handler processes the following types of exceptions:
 * </p>
 * <ul>
 *     <li>{@link WebExchangeBindException} - Validation failures for data binding</li>
 *     <li>{@link HandlerMethodValidationException} - Method parameter validation failures using Visitor pattern</li>
 * </ul>
 * <p>
 * The handler uses the Visitor pattern to process different types of parameter validation results,
 * including annotations like {@code @CookieValue}, {@code @MatrixVariable}, {@code @ModelAttribute},
 * {@code @PathVariable}, {@code @RequestBody}, {@code @RequestHeader}, {@code @RequestParam},
 * and {@code @RequestPart}. All operations return {@link Mono} for reactive processing.
 * </p>
 *
 * @see ResponseEntityExceptionHandler
 * @since 0.0.1-SNAPSHOT
 */
@RestControllerAdvice
public class FluxExtendedProblemDetailExceptionHandler extends ResponseEntityExceptionHandler {

    protected final Log logger = LogFactory.getLog(getClass());

    private final ExtendedProblemDetailLog extendedProblemDetailLog;

    /**
     * Constructs a new handler with the specified log instance.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     */
    public FluxExtendedProblemDetailExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
        this.extendedProblemDetailLog = extendedProblemDetailLog;
    }

    /**
     * Handles web exchange bind exceptions.
     * <p>
     * Converts validation errors from BindingResult into a list of Error objects
     * and wraps them in an ExtendedProblemDetail response.
     * </p>
     *
     * @param ex       the WebExchangeBindException that was thrown
     * @param headers  the HTTP headers to be used in the response
     * @param status   the HTTP status code
     * @param exchange the current server web exchange
     * @return Mono containing ResponseEntity with the ExtendedProblemDetail with validation errors
     */
    @Override
    protected Mono<ResponseEntity<Object>> handleWebExchangeBindException(WebExchangeBindException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        ProblemDetail body = ex.getBody();
        BindingResult bindingResult = ex.getBindingResult();
        List<Error> errors = bindingResult.getAllErrors().stream().map(this::objectErrorToError).toList();
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(body);
        extendedProblemDetail.setErrors(errors);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, exchange);
    }

    /**
     * Handles handler method validation exceptions using Visitor pattern.
     * <p>
     * This method processes validation results for various parameter annotations by visiting
     * each type of validation result and converting them into Error objects. Unsupported
     * validation results are logged as errors.
     * </p>
     *
     * @param ex       the HandlerMethodValidationException that was thrown
     * @param headers  the HTTP headers to be used in the response
     * @param status   the HTTP status code
     * @param exchange the current server web exchange
     * @return Mono containing ResponseEntity with the ExtendedProblemDetail with validation errors
     */
    @Override
    protected Mono<ResponseEntity<Object>> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        List<Error> errorList = processHandlerMethodValidationException(ex);
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(ex.getBody());
        extendedProblemDetail.setErrors(errorList);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, exchange);
    }

    /**
     * Handles method validation exceptions.
     * <p>
     * Converts method-level validation errors into a list of Error objects,
     * logs the validation failure, and wraps them in an ExtendedProblemDetail response.
     * </p>
     *
     * @param ex       the MethodValidationException that was thrown
     * @param status   the HTTP status code
     * @param exchange the current server web exchange
     * @return Mono containing ResponseEntity with the ExtendedProblemDetail with validation errors
     */
    @Override
    protected Mono<ResponseEntity<Object>> handleMethodValidationException(MethodValidationException ex, HttpStatus status, ServerWebExchange exchange) {
        List<Error> errors = methodValidationExceptionToErrors(ex);
        String method = ex.getMethod().getName();
        extendedProblemDetailLog.log(logger, ex, "handleMethodValidationException method = {}, errors = {}", method, errors);
        ProblemDetail body = createProblemDetail(ex, status, "Validation failed", null, null, exchange);
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(body);
        extendedProblemDetail.setErrors(errors);
        return handleExceptionInternal(ex, extendedProblemDetail, null, status, exchange);
    }

    /**
     * Converts an {@link ObjectError} to an {@link Error} object.
     * <p>
     * If the ObjectError is a {@link FieldError}, extracts the field name as the target.
     * </p>
     *
     * @param objectError the ObjectError to convert
     * @return Error object with field and message information
     */
    protected Error objectErrorToError(ObjectError objectError) {
        String target = null;
        if (objectError instanceof FieldError fieldError) {
            target = fieldError.getField();
        }
        return new Error(Error.Type.PARAMETER, target, objectError.getDefaultMessage());
    }

    /**
     * Converts a {@link MethodValidationException} to a list of {@link Error} objects.
     *
     * @param ex the MethodValidationException to convert
     * @return list of Error objects representing all validation errors
     */
    protected List<Error> methodValidationExceptionToErrors(MethodValidationException ex) {
        List<Error> errors = new ArrayList<>();
        ex.getParameterValidationResults().forEach(parameterValidationResult -> {
            if (parameterValidationResult instanceof ParameterErrors parameterErrors) {
                parameterErrors.getAllErrors().stream()
                        .map(this::objectErrorToError)
                        .forEach(errors::add);
            } else {
                String parameterName = parameterValidationResult.getMethodParameter().getParameterName();
                parameterValidationResult.getResolvableErrors().stream()
                        .map(messageSourceResolvable -> new Error(
                                Error.Type.PARAMETER,
                                parameterName,
                                messageSourceResolvable.getDefaultMessage()))
                        .forEach(errors::add);
            }
        });
        ex.getCrossParameterValidationResults().stream()
                .map(parameterValidationResult -> new Error(
                        Error.Type.PARAMETER,
                        null,
                        parameterValidationResult.getDefaultMessage()))
                .forEach(errors::add);
        return errors;
    }

    /**
     * Processes a {@link HandlerMethodValidationException} using the Visitor pattern
     * and converts all validation errors to a list of {@link Error} objects.
     *
     * @param ex the HandlerMethodValidationException to process
     * @return list of Error objects representing all validation errors
     */
    protected List<Error> processHandlerMethodValidationException(HandlerMethodValidationException ex) {
        List<Error> errorList = new ArrayList<>();
        ex.visitResults(new HandlerMethodValidationException.Visitor() {

            @Override
            public void cookieValue(CookieValue cookieValue, ParameterValidationResult result) {
                handleCookieValue(cookieValue, result, errorList);
            }

            @Override
            public void matrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result) {
                handleMatrixVariable(matrixVariable, result, errorList);
            }

            @Override
            public void modelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors) {
                handleModelAttribute(modelAttribute, errors, errorList);
            }

            @Override
            public void pathVariable(PathVariable pathVariable, ParameterValidationResult result) {
                handlePathVariable(pathVariable, result, errorList);
            }

            @Override
            public void requestBody(RequestBody requestBody, ParameterErrors errors) {
                handleRequestBody(requestBody, errors, errorList);
            }

            @Override
            public void requestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result) {
                handleRequestBodyValidationResult(requestBody, result, errorList);
            }

            @Override
            public void requestHeader(RequestHeader requestHeader, ParameterValidationResult result) {
                handleRequestHeader(requestHeader, result, errorList);
            }

            @Override
            public void requestParam(@Nullable RequestParam requestParam, ParameterValidationResult result) {
                handleRequestParam(requestParam, result, errorList);
            }

            @Override
            public void requestPart(RequestPart requestPart, ParameterErrors errors) {
                handleRequestPart(requestPart, errors, errorList);
            }

            @Override
            public void other(ParameterValidationResult result) {
                handleOther(result, errorList);
            }
        });
        return errorList;
    }

    protected void handleCookieValue(CookieValue cookieValue, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.COOKIE, result.getMethodParameter().getParameterName(), errorList);
    }

    protected void handleMatrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.PARAMETER, result.getMethodParameter().getParameterName(), errorList);
    }

    protected void handleModelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors, List<Error> errorList) {
        errors.getAllErrors().stream().map(this::objectErrorToError).forEach(errorList::add);
    }

    protected void handlePathVariable(PathVariable pathVariable, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.PARAMETER, result.getMethodParameter().getParameterName(), errorList);
    }

    protected void handleRequestBody(RequestBody requestBody, ParameterErrors errors, List<Error> errorList) {
        errors.getAllErrors().stream().map(this::objectErrorToError).forEach(errorList::add);
    }

    protected void handleRequestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.PARAMETER, null, errorList);
    }

    protected void handleRequestHeader(RequestHeader requestHeader, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.HEADER, result.getMethodParameter().getParameterName(), errorList);
    }

    protected void handleRequestParam(@Nullable RequestParam requestParam, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.PARAMETER, result.getMethodParameter().getParameterName(), errorList);
    }

    protected void handleRequestPart(RequestPart requestPart, ParameterErrors errors, List<Error> errorList) {
        errors.getAllErrors().stream().map(this::objectErrorToError).forEach(errorList::add);
    }

    protected void handleOther(ParameterValidationResult result, List<Error> errorList) {
        result.getResolvableErrors().forEach(error ->
                extendedProblemDetailLog.log(logger, null, "codes: {}, defaultMessage: {}", error.getCodes(), error.getDefaultMessage()));
    }

    private void addParameterValidationErrors(ParameterValidationResult result, Error.Type errorType,
                                              @Nullable String parameterName, List<Error> errorList) {
        result.getResolvableErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .map(defaultMessage -> new Error(errorType, parameterName, defaultMessage))
                .forEach(errorList::add);
    }
}

package com.github.sbracely.extended.problem.detail.flux.handler;

import com.github.sbracely.extended.problem.detail.core.Error;
import com.github.sbracely.extended.problem.detail.core.ExtendedProblemDetail;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @see MvcExtendedProblemDetailExceptionHandler WebMVC version of exception handler
 * @since 0.0.1-SNAPSHOT
 */
@RestControllerAdvice
public class FluxExtendedProblemDetailExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(FluxExtendedProblemDetailExceptionHandler.class);

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
        List<Error> errors = bindingResult.getAllErrors().stream().map(this::ObjectErrorConvertToError).toList();
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
            public void requestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result) {
                processParameterValidationResult(result, Error.Type.PARAMETER, null);
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
                        log.warn("codes: {}, defaultMessage: {}", error.getCodes(), error.getDefaultMessage()));
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
                errors.getAllErrors().stream().map(FluxExtendedProblemDetailExceptionHandler.this::ObjectErrorConvertToError).forEach(errorList::add);
            }
        });
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(ex.getBody());
        extendedProblemDetail.setErrors(errorList);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleMethodValidationException(MethodValidationException ex, HttpStatus status, ServerWebExchange exchange) {
        List<Error> errors = methodValidationExceptionConvertToError(ex);
        String method = ex.getMethod().getName();
        log.warn("handleMethodValidationException method = {}, errors = {}", method, errors, ex);
        ProblemDetail body = createProblemDetail(ex, status, "Validation failed", null, null, exchange);
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(body);
        extendedProblemDetail.setErrors(errors);
        return handleExceptionInternal(ex, extendedProblemDetail, null, status, exchange);
    }

    private List<Error> methodValidationExceptionConvertToError(MethodValidationException ex) {
        List<Error> errors = new ArrayList<>();
        ex.getParameterValidationResults().forEach(parameterValidationResult -> {
            if (parameterValidationResult instanceof ParameterErrors parameterErrors) {
                parameterErrors.getAllErrors().stream().map(this::ObjectErrorConvertToError).forEach(errors::add);
            } else {
                String parameterName = parameterValidationResult.getMethodParameter().getParameterName();
                parameterValidationResult.getResolvableErrors().stream().map(messageSourceResolvable ->
                        new Error(Error.Type.PARAMETER, parameterName, messageSourceResolvable.getDefaultMessage())
                ).forEach(errors::add);
            }
        });
        ex.getCrossParameterValidationResults().stream().map(parameterValidationResult ->
                new Error(Error.Type.PARAMETER, null, parameterValidationResult.getDefaultMessage())
        ).forEach(errors::add);
        return errors;
    }

    /**
     * Converts an ObjectError to an Error object.
     * <p>
     * If the ObjectError is a FieldError, extracts the field name.
     * Sets the error type to PARAMETER by default.
     * </p>
     *
     * @param objectError the ObjectError to convert
     * @return Error object with field and message information
     */
    private Error ObjectErrorConvertToError(ObjectError objectError) {
        String target = null;
        if (objectError instanceof FieldError fieldError) {
            target = fieldError.getField();
        }
        return new Error(Error.Type.PARAMETER, target, objectError.getDefaultMessage());
    }
}

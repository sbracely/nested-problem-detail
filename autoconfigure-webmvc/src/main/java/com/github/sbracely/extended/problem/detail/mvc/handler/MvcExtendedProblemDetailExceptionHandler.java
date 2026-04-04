package com.github.sbracely.extended.problem.detail.mvc.handler;

import com.github.sbracely.extended.problem.detail.core.handler.ValidationErrorHandler;
import com.github.sbracely.extended.problem.detail.core.logging.ExtendedProblemDetailLog;
import com.github.sbracely.extended.problem.detail.core.response.Error;
import com.github.sbracely.extended.problem.detail.core.response.ExtendedProblemDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * MVC Extended Problem Detail Exception Handler.
 * <p>
 * This exception handler extends Spring's {@link ResponseEntityExceptionHandler} to provide
 * enhanced validation error handling for Spring WebMVC applications. It intercepts validation
 * exceptions and converts them into extended problem detail responses with field-level error information.
 * </p>
 * <p>
 * This handler processes the following types of exceptions:
 * </p>
 * <ul>
 *     <li>{@link MethodArgumentNotValidException} - Validation failures for @Valid annotated arguments</li>
 *     <li>{@link HandlerMethodValidationException} - Method parameter validation failures using Visitor pattern</li>
 *     <li>{@link WebExchangeBindException} - Data binding exceptions</li>
 * </ul>
 * <p>
 * To customize error handling for specific parameter types, extend this class and override
 * the corresponding method in {@link ValidationErrorHandler}, or provide a custom
 * {@link ValidationErrorHandler} implementation.
 * </p>
 *
 * @see ResponseEntityExceptionHandler
 * @see ValidationErrorHandler
 * @since 0.0.1-SNAPSHOT
 */
@RestControllerAdvice
public class MvcExtendedProblemDetailExceptionHandler extends ResponseEntityExceptionHandler {

    protected final Log logger = LogFactory.getLog(getClass());

    protected final ValidationErrorHandler validationErrorHandler;

    protected final ExtendedProblemDetailLog extendedProblemDetailLog;

    /**
     * Constructs a new handler with the specified dependencies.
     *
     * @param validationErrorHandler   the ValidationErrorHandler instance
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     */
    public MvcExtendedProblemDetailExceptionHandler(ValidationErrorHandler validationErrorHandler,
                                                    ExtendedProblemDetailLog extendedProblemDetailLog) {
        this.validationErrorHandler = validationErrorHandler;
        this.extendedProblemDetailLog = extendedProblemDetailLog;
    }

    /**
     * Handles method argument not valid exceptions.
     * <p>
     * Converts validation errors from BindingResult into a list of Error objects
     * and wraps them in an ExtendedProblemDetail response.
     * </p>
     *
     * @param ex      the MethodArgumentNotValidException that was thrown
     * @param headers the HTTP headers to be used in the response
     * @param status  the HTTP status code
     * @param request the current web request
     * @return ResponseEntity containing the ExtendedProblemDetail with validation errors
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<Error> errors = validationErrorHandler.handleMethodArgumentNotValidException(ex);
        ExtendedProblemDetail extendedProblemDetail = ExtendedProblemDetail.from(ex.getBody(), errors);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
    }

    /**
     * Handles handler method validation exceptions using Visitor pattern.
     * <p>
     * This method processes validation results for various parameter annotations by visiting
     * each type of validation result and converting them into Error objects.
     * </p>
     *
     * @param ex      the HandlerMethodValidationException that was thrown
     * @param headers the HTTP headers to be used in the response
     * @param status  the HTTP status code
     * @param request the current web request
     * @return ResponseEntity containing the ExtendedProblemDetail with validation errors
     */
    @Override
    public @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<Error> errorList = validationErrorHandler.handleHandlerMethodValidationException(ex);
        ExtendedProblemDetail extendedProblemDetail = ExtendedProblemDetail.from(ex.getBody(), errorList);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
    }

    /**
     * Handles error response exceptions.
     * <p>
     * Specifically handles WebExchangeBindException to extract validation errors
     * and include them in the extended problem detail response.
     * </p>
     *
     * @param ex      the ErrorResponseException that was thrown
     * @param headers the HTTP headers to be used in the response
     * @param status  the HTTP status code
     * @param request the current web request
     * @return ResponseEntity containing the exception details
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleErrorResponseException(
            ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex instanceof WebExchangeBindException exchangeBindException) {
            List<Error> errors = validationErrorHandler.handleWebExchangeBindException(exchangeBindException);
            exchangeBindException.updateAndGetBody(getMessageSource(), request.getLocale());
            ExtendedProblemDetail extendedProblemDetail = ExtendedProblemDetail.from(ex.getBody(), errors);
            return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
        }
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    /**
     * Handles async request not usable exceptions.
     * <p>
     * Logs the exception and returns null to let Spring handle it with default error handling.
     * </p>
     *
     * @param ex      the AsyncRequestNotUsableException that was thrown
     * @param request the current web request
     * @return null to use default error handling
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleAsyncRequestNotUsableException(
            AsyncRequestNotUsableException ex, WebRequest request) {
        extendedProblemDetailLog.log(logger, ex, "handleAsyncRequestNotUsableException");
        return null;
    }

    /**
     * Handles method validation exceptions.
     * <p>
     * Converts method-level validation errors into a list of Error objects,
     * logs the validation failure, and wraps them in an ExtendedProblemDetail response.
     * </p>
     *
     * @param ex      the MethodValidationException that was thrown
     * @param headers the HTTP headers to be used in the response
     * @param status  the HTTP status code
     * @param request the current web request
     * @return ResponseEntity containing the ExtendedProblemDetail with validation errors
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleMethodValidationException(MethodValidationException ex,
                                                                               HttpHeaders headers,
                                                                               HttpStatus status,
                                                                               WebRequest request) {
        List<Error> errors = validationErrorHandler.handleMethodValidationException(ex);
        String method = ex.getMethod().getName();
        extendedProblemDetailLog.log(logger, ex, "handleMethodValidationException method = {}, errors = {}", method, errors);
        ProblemDetail body = createProblemDetail(ex, status, "Validation failed", null, null, request);
        ExtendedProblemDetail extendedProblemDetail = ExtendedProblemDetail.from(body, errors);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
    }
}

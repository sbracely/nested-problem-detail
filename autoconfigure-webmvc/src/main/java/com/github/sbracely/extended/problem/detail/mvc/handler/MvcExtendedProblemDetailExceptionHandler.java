package com.github.sbracely.extended.problem.detail.mvc.handler;

import com.github.sbracely.extended.problem.detail.core.response.Error;
import com.github.sbracely.extended.problem.detail.core.converter.ErrorConverter;
import com.github.sbracely.extended.problem.detail.core.response.ExtendedProblemDetail;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
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
 * The handler uses the Visitor pattern to process different types of parameter validation results,
 * including annotations like {@code @CookieValue}, {@code @MatrixVariable}, {@code @ModelAttribute},
 * {@code @PathVariable}, {@code @RequestBody}, {@code @RequestHeader}, {@code @RequestParam},
 * and {@code @RequestPart}.
 * </p>
 *
 * @see ResponseEntityExceptionHandler
 * @see ErrorConverter
 * @since 0.0.1-SNAPSHOT
 */
@RestControllerAdvice
public class MvcExtendedProblemDetailExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MvcExtendedProblemDetailExceptionHandler.class);

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
        List<Error> errors = ex.getBindingResult().getAllErrors().stream().map(ErrorConverter::objectErrorConvertToError).toList();
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(ex.getBody());
        extendedProblemDetail.setErrors(errors);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
    }

    /**
     * Handles handler method validation exceptions using Visitor pattern.
     * <p>
     * This method processes validation results for various parameter annotations by visiting
     * each type of validation result and converting them into Error objects. Unsupported
     * validation results are logged as errors.
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
        List<Error> errorList = ErrorConverter.processHandlerMethodValidationException(ex);
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(ex.getBody());
        extendedProblemDetail.setErrors(errorList);
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
            exchangeBindException.updateAndGetBody(getMessageSource(), request.getLocale());
            ProblemDetail body = exchangeBindException.getBody();
            BindingResult bindingResult = exchangeBindException.getBindingResult();
            List<Error> errors = bindingResult.getAllErrors().stream().map(ErrorConverter::objectErrorConvertToError).toList();
            ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(body);
            extendedProblemDetail.setErrors(errors);
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
        log.error("handleAsyncRequestNotUsableException", ex);
        return null;
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodValidationException(MethodValidationException ex,
                                                                               HttpHeaders headers,
                                                                               HttpStatus status,
                                                                               WebRequest request) {
        List<Error> errors = ErrorConverter.methodValidationExceptionConvertToError(ex);
        String method = ex.getMethod().getName();
        log.debug("handleMethodValidationException method = {}, errors = {}", method, errors, ex);
        ProblemDetail body = createProblemDetail(ex, status, "Validation failed", null, null, request);
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(body);
        extendedProblemDetail.setErrors(errors);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
    }
}

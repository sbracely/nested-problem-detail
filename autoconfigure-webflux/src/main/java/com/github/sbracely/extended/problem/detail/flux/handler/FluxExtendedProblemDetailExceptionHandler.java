package com.github.sbracely.extended.problem.detail.flux.handler;

import com.github.sbracely.extended.problem.detail.core.converter.ErrorConverter;
import com.github.sbracely.extended.problem.detail.core.logging.ExtendedProblemDetailLog;
import com.github.sbracely.extended.problem.detail.core.response.Error;
import com.github.sbracely.extended.problem.detail.core.response.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.flux.FluxExtendedProblemDetailProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
 * @see ErrorConverter
 * @see FluxExtendedProblemDetailProperties
 * @since 0.0.1-SNAPSHOT
 */
@RestControllerAdvice
public class FluxExtendedProblemDetailExceptionHandler extends ResponseEntityExceptionHandler {

    protected final Log logger = LogFactory.getLog(getClass());

    private final FluxExtendedProblemDetailProperties properties;

    /**
     * Constructs a new handler with the specified properties.
     *
     * @param properties the configuration properties
     */
    public FluxExtendedProblemDetailExceptionHandler(FluxExtendedProblemDetailProperties properties) {
        this.properties = properties;
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
        List<Error> errors = bindingResult.getAllErrors().stream().map(ErrorConverter::objectErrorConvertToError).toList();
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
        List<Error> errorList = ErrorConverter.processHandlerMethodValidationException(ex);
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(ex.getBody());
        extendedProblemDetail.setErrors(errorList);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleMethodValidationException(MethodValidationException ex, HttpStatus status, ServerWebExchange exchange) {
        List<Error> errors = ErrorConverter.methodValidationExceptionConvertToError(ex);
        String method = ex.getMethod().getName();
        ExtendedProblemDetailLog.log(logger, properties.getLogLevel(), properties.isPrintStackTrace(), ex, "handleMethodValidationException method = {}, errors = {}", method, errors);
        ProblemDetail body = createProblemDetail(ex, status, "Validation failed", null, null, exchange);
        ExtendedProblemDetail extendedProblemDetail = new ExtendedProblemDetail(body);
        extendedProblemDetail.setErrors(errors);
        return handleExceptionInternal(ex, extendedProblemDetail, null, status, exchange);
    }
}

package io.github.sbracely.extended.problem.detail.flux.handler;

import io.github.sbracely.extended.problem.detail.common.handler.ExtendedProblemDetailErrorResolver;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * WebFlux Extended Problem Detail Exception Handler.
 * <p>
 * This exception handler extends Spring WebFlux's {@link ResponseEntityExceptionHandler} to provide
 * enhanced error handling for reactive web applications. It intercepts exceptions
 * and converts them into extended problem detail responses with field-level error information.
 * </p>
 * <p>
 * This handler processes the following types of exceptions:
 * </p>
 * <ul>
 *     <li>{@link WebExchangeBindException} - Data binding exceptions</li>
 *     <li>{@link HandlerMethodValidationException} - Method parameter validation failures using Visitor pattern</li>
 *     <li>{@link MethodValidationException} - Method-level validation failures</li>
 * </ul>
 * <p>
 * To customize error resolving for specific parameter types, extend this class and override
 * the corresponding resolveXxx methods defined in {@link ExtendedProblemDetailErrorResolver}.
 * </p>
 *
 * @see ResponseEntityExceptionHandler
 * @see ExtendedProblemDetailErrorResolver
 * @since 1.0.0
 */
@RestControllerAdvice
public class FluxExtendedProblemDetailExceptionHandler extends ResponseEntityExceptionHandler
        implements ExtendedProblemDetailErrorResolver {

    /**
     * Logger for this exception handler.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Log configuration for Extended Problem Detail exception handling.
     */
    protected final ExtendedProblemDetailLog extendedProblemDetailLog;

    /**
     * Constructs a new handler with the specified dependencies.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     */
    public FluxExtendedProblemDetailExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
        this.extendedProblemDetailLog = extendedProblemDetailLog;
    }

    @Override
    public Log getLog() {
        return logger;
    }

    @Override
    public ExtendedProblemDetailLog getExtendedProblemDetailLog() {
        return extendedProblemDetailLog;
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleMethodNotAllowedException(MethodNotAllowedException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleMethodNotAllowedException");
        return super.handleMethodNotAllowedException(ex, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleNotAcceptableStatusException(NotAcceptableStatusException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleNotAcceptableStatusException");
        return super.handleNotAcceptableStatusException(ex, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleUnsupportedMediaTypeStatusException(UnsupportedMediaTypeStatusException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleUnsupportedMediaTypeStatusException");
        return super.handleUnsupportedMediaTypeStatusException(ex, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleMissingRequestValueException(MissingRequestValueException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleMissingRequestValueException");
        return super.handleMissingRequestValueException(ex, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleUnsatisfiedRequestParameterException(UnsatisfiedRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleUnsatisfiedRequestParameterException");
        return super.handleUnsatisfiedRequestParameterException(ex, headers, status, exchange);
    }

    /**
     * Handles web exchange bind exceptions.
     * <p>
     * Converts errors from BindingResult into a list of Error objects
     * and wraps them in an ExtendedProblemDetail response.
     * </p>
     *
     * @param ex       the WebExchangeBindException that was thrown
     * @param headers  the HTTP headers to be used in the response
     * @param status   the HTTP status code
     * @param exchange the current server web exchange
     * @return Mono containing ResponseEntity with the ExtendedProblemDetail with errors
     */
    @Override
    protected Mono<ResponseEntity<Object>> handleWebExchangeBindException(WebExchangeBindException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleWebExchangeBindException");
        List<Error> errors = resolveWebExchangeBindException(ex);
        ExtendedProblemDetail extendedProblemDetail = ExtendedProblemDetail.from(ex.getBody(), errors);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, exchange);
    }

    /**
     * Resolves errors from {@link WebExchangeBindException}.
     *
     * @param ex the WebExchangeBindException to resolve
     * @return list of Error objects representing all errors
     */
    protected List<Error> resolveWebExchangeBindException(WebExchangeBindException ex) {
        return resolveBindingResult(ex.getBindingResult());
    }

    /**
     * Handles handler method validation exceptions using Visitor pattern.
     * <p>
     * This method processes validation results for various parameter annotations by visiting
     * each type of validation result and converting them into Error objects.
     * </p>
     *
     * @param ex       the HandlerMethodValidationException that was thrown
     * @param headers  the HTTP headers to be used in the response
     * @param status   the HTTP status code
     * @param exchange the current server web exchange
     * @return Mono containing ResponseEntity with the ExtendedProblemDetail with errors
     */
    @Override
    protected Mono<ResponseEntity<Object>> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, true, true, "handleHandlerMethodValidationException");
        List<Error> errorList = resolveHandlerMethodValidationException(ex);
        ExtendedProblemDetail extendedProblemDetail = ExtendedProblemDetail.from(ex.getBody(), errorList);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleServerWebInputException(ServerWebInputException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleServerWebInputException");
        return super.handleServerWebInputException(ex, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleServerErrorException(ServerErrorException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleServerErrorException");
        return super.handleServerErrorException(ex, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleResponseStatusException(ResponseStatusException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleResponseStatusException");
        return super.handleResponseStatusException(ex, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleErrorResponseException(ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleErrorResponseException");
        return super.handleErrorResponseException(ex, headers, status, exchange);
    }

    /**
     * Handles method validation exceptions.
     * <p>
     * Converts method-level errors into a list of Error objects,
     * logs the failure, and wraps them in an ExtendedProblemDetail response.
     * </p>
     *
     * @param ex       the MethodValidationException that was thrown
     * @param status   the HTTP status code
     * @param exchange the current server web exchange
     * @return Mono containing ResponseEntity with the ExtendedProblemDetail with errors
     */
    @Override
    protected Mono<ResponseEntity<Object>> handleMethodValidationException(MethodValidationException ex, HttpStatus status, ServerWebExchange exchange) {
        extendedProblemDetailLog.log(logger, ex, "handleMethodValidationException");
        List<Error> errors = resolveMethodValidationException(ex);
        ProblemDetail body = createProblemDetail(ex, status, "Validation failed", null, null, exchange);
        ExtendedProblemDetail extendedProblemDetail = ExtendedProblemDetail.from(body, errors);
        return handleExceptionInternal(ex, extendedProblemDetail, null, status, exchange);
    }
}

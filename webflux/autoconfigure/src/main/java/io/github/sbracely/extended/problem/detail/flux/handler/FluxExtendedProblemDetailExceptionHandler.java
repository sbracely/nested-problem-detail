package io.github.sbracely.extended.problem.detail.flux.handler;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
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
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
 * the corresponding resolveXxx methods.
 * </p>
 *
 * @see ResponseEntityExceptionHandler
 * @since 1.0.0
 */
@RestControllerAdvice
public class FluxExtendedProblemDetailExceptionHandler extends ResponseEntityExceptionHandler {

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
        extendedProblemDetailLog.log(logger, ex, "handleHandlerMethodValidationException");
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

    // ==================== Error Resolving Methods ====================

    /**
     * Resolves errors from {@link WebExchangeBindException}.
     *
     * @param ex the WebExchangeBindException to resolve
     * @return list of Error objects representing all errors
     */
    protected List<Error> resolveWebExchangeBindException(WebExchangeBindException ex) {
        extendedProblemDetailLog.log(logger, ex, "resolveWebExchangeBindException");
        return resolveBindingResult(ex.getBindingResult());
    }

    /**
     * Resolves errors from {@link HandlerMethodValidationException} using the Visitor pattern.
     *
     * @param ex the HandlerMethodValidationException to resolve
     * @return list of Error objects representing all errors
     */
    protected List<Error> resolveHandlerMethodValidationException(HandlerMethodValidationException ex) {
        List<Error> errorList = new ArrayList<>();
        ex.visitResults(new HandlerMethodValidationException.Visitor() {

            @Override
            public void cookieValue(CookieValue cookieValue, ParameterValidationResult result) {
                resolveCookieValue(ex, cookieValue, result, errorList);
            }

            @Override
            public void matrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result) {
                resolveMatrixVariable(ex, matrixVariable, result, errorList);
            }

            @Override
            public void modelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors) {
                resolveModelAttribute(ex, modelAttribute, errors, errorList);
            }

            @Override
            public void pathVariable(PathVariable pathVariable, ParameterValidationResult result) {
                resolvePathVariable(ex, pathVariable, result, errorList);
            }

            @Override
            public void requestBody(RequestBody requestBody, ParameterErrors errors) {
                resolveRequestBody(ex, requestBody, errors, errorList);
            }

            @Override
            public void requestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result) {
                resolveRequestBodyValidationResult(ex, requestBody, result, errorList);
            }

            @Override
            public void requestHeader(RequestHeader requestHeader, ParameterValidationResult result) {
                resolveRequestHeader(ex, requestHeader, result, errorList);
            }

            @Override
            public void requestParam(@Nullable RequestParam requestParam, ParameterValidationResult result) {
                resolveRequestParam(ex, requestParam, result, errorList);
            }

            @Override
            public void requestPart(RequestPart requestPart, ParameterErrors errors) {
                resolveRequestPart(ex, requestPart, errors, errorList);
            }

            @Override
            public void other(ParameterValidationResult result) {
                resolveOther(ex, result, errorList);
            }
        });
        return errorList;
    }

    /**
     * Resolves errors from cookie value parameter.
     * <p>
     * Override this method to customize how {@code @CookieValue} parameter
     * errors are converted to Error objects.
     * </p>
     *
     * @param ex          the HandlerMethodValidationException being processed
     * @param cookieValue the CookieValue annotation
     * @param result      the parameter validation result
     * @param errorList   the list to populate with errors
     */
    protected void resolveCookieValue(HandlerMethodValidationException ex, CookieValue cookieValue, ParameterValidationResult result, List<Error> errorList) {
        extendedProblemDetailLog.log(logger, ex, "resolveCookieValue");
        addParameterErrors(result, Error.Type.COOKIE,
                result.getMethodParameter().getParameterName(), errorList);
    }

    /**
     * Resolves errors from matrix variable parameter.
     * <p>
     * Override this method to customize how {@code @MatrixVariable} parameter
     * errors are converted to Error objects.
     * </p>
     *
     * @param ex             the HandlerMethodValidationException being processed
     * @param matrixVariable the MatrixVariable annotation
     * @param result         the parameter validation result
     * @param errorList      the list to populate with errors
     */
    protected void resolveMatrixVariable(HandlerMethodValidationException ex, MatrixVariable matrixVariable, ParameterValidationResult result, List<Error> errorList) {
        extendedProblemDetailLog.log(logger, ex, "resolveMatrixVariable");
        addParameterErrors(result, Error.Type.PARAMETER,
                result.getMethodParameter().getParameterName(), errorList);
    }

    /**
     * Resolves errors from model attribute parameter.
     * <p>
     * Override this method to customize how {@code @ModelAttribute} parameter
     * errors are converted to Error objects.
     * </p>
     *
     * @param ex             the HandlerMethodValidationException being processed
     * @param modelAttribute the ModelAttribute annotation (may be null)
     * @param errors         the parameter errors
     * @param errorList      the list to populate with errors
     */
    protected void resolveModelAttribute(HandlerMethodValidationException ex, @Nullable ModelAttribute modelAttribute, ParameterErrors errors, List<Error> errorList) {
        extendedProblemDetailLog.log(logger, ex, "resolveModelAttribute");
        errors.getAllErrors().stream()
                .map(this::objectErrorToError)
                .forEach(errorList::add);
    }

    /**
     * Resolves errors from path variable parameter.
     * <p>
     * Override this method to customize how {@code @PathVariable} parameter
     * errors are converted to Error objects.
     * </p>
     *
     * @param ex           the HandlerMethodValidationException being processed
     * @param pathVariable the PathVariable annotation
     * @param result       the parameter validation result
     * @param errorList    the list to populate with errors
     */
    protected void resolvePathVariable(HandlerMethodValidationException ex, PathVariable pathVariable, ParameterValidationResult result, List<Error> errorList) {
        extendedProblemDetailLog.log(logger, ex, "resolvePathVariable");
        addParameterErrors(result, Error.Type.PARAMETER,
                result.getMethodParameter().getParameterName(), errorList);
    }

    /**
     * Resolves errors from request body parameter (ParameterErrors variant).
     * <p>
     * Override this method to customize how {@code @RequestBody} parameter
     * errors are converted to Error objects.
     * </p>
     *
     * @param ex          the HandlerMethodValidationException being processed
     * @param requestBody the RequestBody annotation
     * @param errors      the parameter errors
     * @param errorList   the list to populate with errors
     */
    protected void resolveRequestBody(HandlerMethodValidationException ex, RequestBody requestBody, ParameterErrors errors, List<Error> errorList) {
        extendedProblemDetailLog.log(logger, ex, "resolveRequestBody");
        errors.getAllErrors().stream()
                .map(this::objectErrorToError)
                .forEach(errorList::add);
    }

    /**
     * Resolves errors from request body parameter (ParameterValidationResult variant).
     * <p>
     * Override this method to customize how {@code @RequestBody} validation
     * results are converted to Error objects.
     * </p>
     *
     * @param ex          the HandlerMethodValidationException being processed
     * @param requestBody the RequestBody annotation
     * @param result      the parameter validation result
     * @param errorList   the list to populate with errors
     */
    protected void resolveRequestBodyValidationResult(HandlerMethodValidationException ex, RequestBody requestBody, ParameterValidationResult result, List<Error> errorList) {
        extendedProblemDetailLog.log(logger, ex, "resolveRequestBodyValidationResult");
        addParameterErrors(result, Error.Type.PARAMETER, null, errorList);
    }

    /**
     * Resolves errors from request header parameter.
     * <p>
     * Override this method to customize how {@code @RequestHeader} parameter
     * errors are converted to Error objects.
     * </p>
     *
     * @param ex            the HandlerMethodValidationException being processed
     * @param requestHeader the RequestHeader annotation
     * @param result        the parameter validation result
     * @param errorList     the list to populate with errors
     */
    protected void resolveRequestHeader(HandlerMethodValidationException ex, RequestHeader requestHeader, ParameterValidationResult result, List<Error> errorList) {
        extendedProblemDetailLog.log(logger, ex, "resolveRequestHeader");
        addParameterErrors(result, Error.Type.HEADER,
                result.getMethodParameter().getParameterName(), errorList);
    }

    /**
     * Resolves errors from request parameter.
     * <p>
     * Override this method to customize how {@code @RequestParam} parameter
     * errors are converted to Error objects.
     * </p>
     *
     * @param ex           the HandlerMethodValidationException being processed
     * @param requestParam the RequestParam annotation (may be null)
     * @param result       the parameter validation result
     * @param errorList    the list to populate with errors
     */
    protected void resolveRequestParam(HandlerMethodValidationException ex, @Nullable RequestParam requestParam, ParameterValidationResult result, List<Error> errorList) {
        extendedProblemDetailLog.log(logger, ex, "resolveRequestParam");
        addParameterErrors(result, Error.Type.PARAMETER,
                result.getMethodParameter().getParameterName(), errorList);
    }

    /**
     * Resolves errors from request part parameter.
     * <p>
     * Override this method to customize how {@code @RequestPart} parameter
     * errors are converted to Error objects.
     * </p>
     *
     * @param ex          the HandlerMethodValidationException being processed
     * @param requestPart the RequestPart annotation
     * @param errors      the parameter errors
     * @param errorList   the list to populate with errors
     */
    protected void resolveRequestPart(HandlerMethodValidationException ex, RequestPart requestPart, ParameterErrors errors, List<Error> errorList) {
        extendedProblemDetailLog.log(logger, ex, "resolveRequestPart");
        errors.getAllErrors().stream()
                .map(this::objectErrorToError)
                .forEach(errorList::add);
    }

    /**
     * Resolves errors from other parameter types.
     * <p>
     * Override this method to customize how unsupported parameter types
     * are handled during error processing.
     * </p>
     *
     * @param ex        the HandlerMethodValidationException being processed
     * @param result    the parameter validation result
     * @param errorList the list to populate with errors
     */
    protected void resolveOther(HandlerMethodValidationException ex, ParameterValidationResult result, List<Error> errorList) {
        extendedProblemDetailLog.log(logger, ex, "resolveOther");
    }

    /**
     * Adds parameter errors to the error list.
     *
     * @param result        the parameter validation result
     * @param errorType     the error type
     * @param parameterName the parameter name
     * @param errorList     the list to populate with errors
     */
    protected void addParameterErrors(ParameterValidationResult result, Error.Type errorType,
                                      @Nullable String parameterName, List<Error> errorList) {
        result.getResolvableErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .map(defaultMessage -> new Error(errorType, parameterName, defaultMessage))
                .forEach(errorList::add);
    }

    /**
     * Resolves errors from {@link MethodValidationException}.
     *
     * @param ex the MethodValidationException to resolve
     * @return list of Error objects representing all errors
     */
    protected List<Error> resolveMethodValidationException(MethodValidationException ex) {
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
     * Converts a {@link BindingResult} to a list of {@link Error} objects.
     *
     * @param bindingResult the BindingResult to convert
     * @return list of Error objects representing all errors
     */
    protected List<Error> resolveBindingResult(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(this::objectErrorToError)
                .toList();
    }

    /**
     * Converts an {@link ObjectError} to an {@link Error} object.
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
}

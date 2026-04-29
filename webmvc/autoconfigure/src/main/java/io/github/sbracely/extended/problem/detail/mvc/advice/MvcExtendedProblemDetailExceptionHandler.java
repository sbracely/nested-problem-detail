package io.github.sbracely.extended.problem.detail.mvc.advice;

import io.github.sbracely.extended.problem.detail.common.error.resolver.ExtendedProblemDetailErrorResolver;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * MVC Extended Problem Detail Exception Handler.
 * <p>
 * This exception handler extends Spring's {@link ResponseEntityExceptionHandler} to provide
 * enhanced error handling for Spring WebMVC applications. It intercepts exceptions
 * and converts them into extended problem detail responses with field-level error information.
 * </p>
 * <p>
 * This handler processes the following types of exceptions:
 * </p>
 * <ul>
 *     <li>{@link MethodArgumentNotValidException} - Validation failures for @Valid annotated arguments</li>
 *     <li>{@link HandlerMethodValidationException} - Method parameter validation failures using Visitor pattern</li>
 *     <li>{@link WebExchangeBindException} - Data binding exceptions</li>
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
@ControllerAdvice
public class MvcExtendedProblemDetailExceptionHandler extends ResponseEntityExceptionHandler
        implements ExtendedProblemDetailErrorResolver {

    /**
     * Logger for this exception handler.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Log configuration for Extended Problem Detail exception handling.
     */
    protected final @Nullable ExtendedProblemDetailLog extendedProblemDetailLog;

    /**
     * Constructs a new handler with the specified dependencies.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance, or {@code null} when logging is disabled
     */
    public MvcExtendedProblemDetailExceptionHandler(@Nullable ExtendedProblemDetailLog extendedProblemDetailLog) {
        this.extendedProblemDetailLog = extendedProblemDetailLog;
    }

    @Override
    public Log getLogger() {
        return logger;
    }

    @Override
    public @Nullable ExtendedProblemDetailLog getExtendedProblemDetailLog() {
        return extendedProblemDetailLog;
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleHttpRequestMethodNotSupported");
        return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleHttpMediaTypeNotSupported");
        return super.handleHttpMediaTypeNotSupported(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleHttpMediaTypeNotAcceptable");
        return super.handleHttpMediaTypeNotAcceptable(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleMissingPathVariable");
        return super.handleMissingPathVariable(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleMissingServletRequestParameter");
        return super.handleMissingServletRequestParameter(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleMissingServletRequestPart");
        return super.handleMissingServletRequestPart(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleServletRequestBindingException");
        return super.handleServletRequestBindingException(ex, headers, status, request);
    }

    /**
     * Handles method argument not valid exceptions.
     * <p>
     * Converts errors from BindingResult into a list of Error objects
     * and wraps them in an ExtendedProblemDetail response.
     * </p>
     *
     * @param ex      the MethodArgumentNotValidException that was thrown
     * @param headers the HTTP headers to be used in the response
     * @param status  the HTTP status code
     * @param request the current web request
     * @return ResponseEntity containing the ExtendedProblemDetail with errors
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleMethodArgumentNotValid");
        List<Error> errors = resolveMethodArgumentNotValidException(ex);
        ExtendedProblemDetail extendedProblemDetail = ExtendedProblemDetail.from(ex.getBody(), errors);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
    }

    /**
     * Resolves errors from {@link MethodArgumentNotValidException}.
     * <p>
     * This method is specific to WebMVC and resolves errors for
     * {@code @Valid} annotated method arguments.
     * </p>
     *
     * @param ex the MethodArgumentNotValidException to resolve
     * @return list of Error objects representing all errors
     */
    protected List<Error> resolveMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return resolveBindingResult(ex.getBindingResult());
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
     * @return ResponseEntity containing the ExtendedProblemDetail with errors
     */
    @Override
    public @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "[exception#{}] handleHandlerMethodValidationException",
                Integer.toHexString(System.identityHashCode(ex)));
        List<Error> errorList = resolveHandlerMethodValidationException(ex);
        ExtendedProblemDetail extendedProblemDetail = ExtendedProblemDetail.from(ex.getBody(), errorList);
        return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleNoHandlerFoundException");
        return super.handleNoHandlerFoundException(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleNoResourceFoundException");
        return super.handleNoResourceFoundException(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleAsyncRequestTimeoutException");
        return super.handleAsyncRequestTimeoutException(ex, headers, status, request);
    }

    /**
     * Handles error response exceptions.
     * <p>
     * Specifically handles WebExchangeBindException to extract errors
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
        log(ex, "handleErrorResponseException");
        if (ex instanceof WebExchangeBindException exchangeBindException) {
            List<Error> errors = resolveWebExchangeBindException(exchangeBindException);
            exchangeBindException.updateAndGetBody(getMessageSource(), request.getLocale());
            ExtendedProblemDetail extendedProblemDetail = ExtendedProblemDetail.from(ex.getBody(), errors);
            return handleExceptionInternal(ex, extendedProblemDetail, headers, status, request);
        }
        return handleExceptionInternal(ex, null, headers, status, request);
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

    @Override
    protected @Nullable ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleMaxUploadSizeExceededException");
        return super.handleMaxUploadSizeExceededException(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleConversionNotSupported");
        return super.handleConversionNotSupported(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleTypeMismatch");
        return super.handleTypeMismatch(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleHttpMessageNotReadable");
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log(ex, "handleHttpMessageNotWritable");
        return super.handleHttpMessageNotWritable(ex, headers, status, request);
    }

    /**
     * Handles method validation exceptions.
     * <p>
     * Converts method-level errors into a list of Error objects,
     * logs the failure, and wraps them in an ExtendedProblemDetail response.
     * </p>
     *
     * @param ex      the MethodValidationException that was thrown
     * @param headers the HTTP headers to be used in the response
     * @param status  the HTTP status code
     * @param request the current web request
     * @return ResponseEntity containing the ExtendedProblemDetail with errors
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleMethodValidationException(MethodValidationException ex,
                                                                               HttpHeaders headers,
                                                                               HttpStatus status,
                                                                               WebRequest request) {
        log(ex, "[exception#{}] handleMethodValidationException",
                Integer.toHexString(System.identityHashCode(ex)));
        resolveMethodValidationException(ex);
        return super.handleMethodValidationException(ex, headers, status, request);
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
        log(ex, "handleAsyncRequestNotUsableException");
        return super.handleAsyncRequestNotUsableException(ex, request);
    }
}

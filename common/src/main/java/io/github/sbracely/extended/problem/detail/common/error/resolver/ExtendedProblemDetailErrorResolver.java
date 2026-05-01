package io.github.sbracely.extended.problem.detail.common.error.resolver;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import org.apache.commons.logging.Log;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Shared error resolving logic for Extended Problem Detail exception handlers.
 * <p>
 * This interface centralizes the common error-resolution strategies used by both
 * Spring WebMVC and Spring WebFlux exception handlers, eliminating duplication
 * between {@code MvcExtendedProblemDetailExceptionHandler} and
 * {@code FluxExtendedProblemDetailExceptionHandler}.
 * </p>
 * <p>
 * Implementing classes must expose their {@link Log} and optional {@link ExtendedProblemDetailLog}
 * via {@link #getLogger()} and {@link #getExtendedProblemDetailLog()} so that the shared
 * {@code default} implementations can log consistently.
 * </p>
 * <p>
 * All {@code resolve*} methods provide {@code default} implementations that cover the
 * standard resolution strategy and may be overridden to customize error conversion for
 * specific annotation types.
 * </p>
 *
 * @since 1.1.0
 */
public interface ExtendedProblemDetailErrorResolver {

    /**
     * Returns the logger used by the implementing handler.
     *
     * @return the commons-logging {@link Log} instance
     */
    Log getLogger();

    /**
     * Returns the extended problem detail log helper used by the implementing handler.
     *
     * @return the {@link ExtendedProblemDetailLog} instance, or {@code null} when logging is disabled
     */
    @Nullable
    ExtendedProblemDetailLog getExtendedProblemDetailLog();

    /**
     * Resolves errors from {@link HandlerMethodValidationException} using the Visitor pattern.
     *
     * @param ex the HandlerMethodValidationException to resolve
     * @return list of Error objects representing all validation errors
     */
    default List<Error> resolveHandlerMethodValidationException(HandlerMethodValidationException ex) {
        List<Error> errorList = new ArrayList<>();
        ExtendedProblemDetailErrorResolver self = this;
        ex.visitResults(new HandlerMethodValidationException.Visitor() {

            @Override
            public void cookieValue(CookieValue cookieValue, ParameterValidationResult result) {
                self.resolveCookieValue(ex, cookieValue, result, errorList);
            }

            @Override
            public void matrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result) {
                self.resolveMatrixVariable(ex, matrixVariable, result, errorList);
            }

            @Override
            public void modelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors) {
                self.resolveModelAttribute(ex, modelAttribute, errors, errorList);
            }

            @Override
            public void pathVariable(PathVariable pathVariable, ParameterValidationResult result) {
                self.resolvePathVariable(ex, pathVariable, result, errorList);
            }

            @Override
            public void requestBody(RequestBody requestBody, ParameterErrors errors) {
                self.resolveRequestBody(ex, requestBody, errors, errorList);
            }

            @Override
            public void requestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result) {
                self.resolveRequestBodyValidationResult(ex, requestBody, result, errorList);
            }

            @Override
            public void requestHeader(RequestHeader requestHeader, ParameterValidationResult result) {
                self.resolveRequestHeader(ex, requestHeader, result, errorList);
            }

            @Override
            public void requestParam(@Nullable RequestParam requestParam, ParameterValidationResult result) {
                self.resolveRequestParam(ex, requestParam, result, errorList);
            }

            @Override
            public void requestPart(RequestPart requestPart, ParameterErrors errors) {
                self.resolveRequestPart(ex, requestPart, errors, errorList);
            }

            @Override
            public void other(ParameterValidationResult result) {
                self.resolveOther(ex, result, errorList);
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
    default void resolveCookieValue(HandlerMethodValidationException ex, CookieValue cookieValue,
                                    ParameterValidationResult result, List<Error> errorList) {
        log("[exception#" + Integer.toHexString(System.identityHashCode(ex)) + "] resolveCookieValue");
        addParameterErrors(result, Error.Type.COOKIE, result.getMethodParameter().getParameterName(), errorList);
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
    default void resolveMatrixVariable(HandlerMethodValidationException ex, MatrixVariable matrixVariable,
                                       ParameterValidationResult result, List<Error> errorList) {
        log("[exception#" + Integer.toHexString(System.identityHashCode(ex)) + "] resolveMatrixVariable");
        addParameterErrors(result, Error.Type.PARAMETER, result.getMethodParameter().getParameterName(), errorList);
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
    default void resolveModelAttribute(HandlerMethodValidationException ex, @Nullable ModelAttribute modelAttribute,
                                       ParameterErrors errors, List<Error> errorList) {
        log("[exception#" + Integer.toHexString(System.identityHashCode(ex)) + "] resolveModelAttribute");
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
    default void resolvePathVariable(HandlerMethodValidationException ex, PathVariable pathVariable,
                                     ParameterValidationResult result, List<Error> errorList) {
        log("[exception#" + Integer.toHexString(System.identityHashCode(ex)) + "] resolvePathVariable");
        addParameterErrors(result, Error.Type.PARAMETER, result.getMethodParameter().getParameterName(), errorList);
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
    default void resolveRequestBody(HandlerMethodValidationException ex, RequestBody requestBody,
                                    ParameterErrors errors, List<Error> errorList) {
        log("[exception#" + Integer.toHexString(System.identityHashCode(ex)) + "] resolveRequestBody");
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
    default void resolveRequestBodyValidationResult(HandlerMethodValidationException ex, RequestBody requestBody,
                                                    ParameterValidationResult result, List<Error> errorList) {
        log("[exception#" + Integer.toHexString(System.identityHashCode(ex)) + "] resolveRequestBodyValidationResult");
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
    default void resolveRequestHeader(HandlerMethodValidationException ex, RequestHeader requestHeader,
                                      ParameterValidationResult result, List<Error> errorList) {
        log("[exception#" + Integer.toHexString(System.identityHashCode(ex)) + "] resolveRequestHeader");
        addParameterErrors(result, Error.Type.HEADER, result.getMethodParameter().getParameterName(), errorList);
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
    default void resolveRequestParam(HandlerMethodValidationException ex, @Nullable RequestParam requestParam,
                                     ParameterValidationResult result, List<Error> errorList) {
        log("[exception#" + Integer.toHexString(System.identityHashCode(ex)) + "] resolveRequestParam");
        addParameterErrors(result, Error.Type.PARAMETER, result.getMethodParameter().getParameterName(), errorList);
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
    default void resolveRequestPart(HandlerMethodValidationException ex, RequestPart requestPart,
                                    ParameterErrors errors, List<Error> errorList) {
        log("[exception#" + Integer.toHexString(System.identityHashCode(ex)) + "] resolveRequestPart");
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
    default void resolveOther(HandlerMethodValidationException ex, ParameterValidationResult result,
                              List<Error> errorList) {
        log("[exception#" + Integer.toHexString(System.identityHashCode(ex)) + "] resolveOther");
    }


    /**
     * Resolves and logs diagnostic messages from {@link MethodValidationException}.
     *
     * @param ex the MethodValidationException to resolve
     */
    default void resolveMethodValidationException(MethodValidationException ex) {
        List<String> validationMessages = new ArrayList<>();

        ex.getParameterValidationResults().stream()
                .flatMap(this::parameterValidationMessages)
                .forEach(validationMessages::add);

        ex.getCrossParameterValidationResults().stream()
                .map(result -> methodValidationMessage(
                        "cross-parameter",
                        result.getDefaultMessage()))
                .forEach(validationMessages::add);

        log("[exception#" + Integer.toHexString(System.identityHashCode(ex))
                + "] MethodValidationException validation errors: " + validationMessages);
    }

    /**
     * Resolves diagnostic messages from one method parameter validation result.
     *
     * @param result the method parameter validation result
     * @return stream of diagnostic messages
     */
    private Stream<String> parameterValidationMessages(ParameterValidationResult result) {
        if (result instanceof ParameterErrors parameterErrors) {
            String parameterName = result.getMethodParameter().getParameterName();
            return parameterErrors.getAllErrors().stream()
                    .map(error -> objectErrorToMessage(parameterName, error));
        }

        String parameterName = result.getMethodParameter().getParameterName();
        return result.getResolvableErrors().stream()
                .map(error -> methodValidationMessage(parameterName, error.getDefaultMessage()));
    }

    /**
     * Converts an {@link ObjectError} to a diagnostic message for server-side logging.
     *
     * @param parameterName the method parameter name
     * @param objectError   the object error to convert
     * @return diagnostic message with target and validation message
     */
    private String objectErrorToMessage(@Nullable String parameterName, ObjectError objectError) {
        String target = parameterName;
        if (objectError instanceof FieldError fieldError) {
            target = methodValidationTarget(parameterName, fieldError.getField());
        } else if (target == null || target.isBlank()) {
            target = objectError.getObjectName();
        }
        return methodValidationMessage(target, objectError.getDefaultMessage());
    }

    /**
     * Builds a method validation target path.
     *
     * @param parameterName the method parameter name
     * @param fieldName     the field name within the parameter object
     * @return target path that distinguishes method parameters from nested fields
     */
    private String methodValidationTarget(@Nullable String parameterName, String fieldName) {
        if (parameterName == null || parameterName.isBlank()) {
            return fieldName;
        }
        if (fieldName == null || fieldName.isBlank()) {
            return parameterName;
        }
        return parameterName + "." + fieldName;
    }

    /**
     * Formats a method validation diagnostic message for server-side logging.
     *
     * @param target  the validated target
     * @param message the validation message
     * @return formatted diagnostic message
     */
    private String methodValidationMessage(@Nullable String target, @Nullable String message) {
        if (target == null || target.isBlank()) {
            return message == null ? "" : message;
        }
        if (message == null || message.isBlank()) {
            return target;
        }
        return target + ": " + message;
    }

    /**
     * Converts a {@link BindingResult} to a list of {@link Error} objects.
     *
     * @param bindingResult the BindingResult to convert
     * @return list of Error objects representing all errors
     */
    default List<Error> resolveBindingResult(BindingResult bindingResult) {
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
    default Error objectErrorToError(ObjectError objectError) {
        String target = null;
        if (objectError instanceof FieldError fieldError) {
            target = fieldError.getField();
        }
        return new Error(Error.Type.PARAMETER, target, objectError.getDefaultMessage());
    }

    /**
     * Adds parameter errors to the error list.
     *
     * @param result        the parameter validation result
     * @param errorType     the error type
     * @param parameterName the parameter name
     * @param errorList     the list to populate with errors
     */
    default void addParameterErrors(ParameterValidationResult result, Error.Type errorType,
                                    @Nullable String parameterName, List<Error> errorList) {
        result.getResolvableErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .map(defaultMessage -> new Error(errorType, parameterName, defaultMessage))
                .forEach(errorList::add);
    }

    /**
     * Logs a correlated message without an exception.
     *
     * @param message the message to log
     */
    default void log(String message) {
        if (getExtendedProblemDetailLog() != null) {
            getExtendedProblemDetailLog().log(getLogger(), message);
        }
    }

    /**
     * Logs a message with an optional exception.
     *
     * @param throwable the exception to log, or {@code null} for message-only logging
     * @param message   the message to log
     */
    default void log(@Nullable Throwable throwable, String message) {
        if (getExtendedProblemDetailLog() != null) {
            getExtendedProblemDetailLog().log(getLogger(), throwable, message);
        }
    }
}

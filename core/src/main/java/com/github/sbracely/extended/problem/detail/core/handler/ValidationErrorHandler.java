package com.github.sbracely.extended.problem.detail.core.handler;

import com.github.sbracely.extended.problem.detail.core.logging.ExtendedProblemDetailLog;
import com.github.sbracely.extended.problem.detail.core.response.Error;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Component for handling validation errors and converting them to {@link Error} objects.
 * <p>
 * This class provides reusable logic for converting various Spring validation exceptions
 * into a unified {@link Error} representation. It is used by both WebMVC and WebFlux
 * exception handlers to eliminate code duplication.
 * </p>
 * <p>
 * To customize error handling, you can:
 * </p>
 * <ul>
 *     <li>Extend this class and override specific {@code handleXxx} methods</li>
 *     <li>Create a custom handler that delegates to this class</li>
 * </ul>
 *
 * @since 0.0.1-SNAPSHOT
 */
public class ValidationErrorHandler {

    private static final Log logger = LogFactory.getLog(ValidationErrorHandler.class);

    private final ExtendedProblemDetailLog extendedProblemDetailLog;

    /**
     * Constructs a new handler with the specified log instance.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     */
    public ValidationErrorHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
        this.extendedProblemDetailLog = extendedProblemDetailLog;
    }

    /**
     * Handles {@link MethodArgumentNotValidException} and returns a list of {@link Error} objects.
     *
     * @param ex the MethodArgumentNotValidException to handle
     * @return list of Error objects representing all validation errors
     */
    public List<Error> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return handleBindingResult(ex.getBindingResult());
    }

    /**
     * Handles {@link WebExchangeBindException} and returns a list of {@link Error} objects.
     *
     * @param ex the WebExchangeBindException to handle
     * @return list of Error objects representing all validation errors
     */
    public List<Error> handleWebExchangeBindException(WebExchangeBindException ex) {
        return handleBindingResult(ex.getBindingResult());
    }

    /**
     * Handles {@link HandlerMethodValidationException} using the Visitor pattern
     * and returns a list of {@link Error} objects.
     *
     * @param ex the HandlerMethodValidationException to handle
     * @return list of Error objects representing all validation errors
     */
    public List<Error> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
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

    /**
     * Handles {@link MethodValidationException} and returns a list of {@link Error} objects.
     *
     * @param ex the MethodValidationException to handle
     * @return list of Error objects representing all validation errors
     */
    public List<Error> handleMethodValidationException(MethodValidationException ex) {
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
     * @return list of Error objects representing all validation errors
     */
    private List<Error> handleBindingResult(BindingResult bindingResult) {
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
    private Error objectErrorToError(ObjectError objectError) {
        String target = null;
        if (objectError instanceof FieldError fieldError) {
            target = fieldError.getField();
        }
        return new Error(Error.Type.PARAMETER, target, objectError.getDefaultMessage());
    }

    /**
     * Handles cookie value validation errors.
     * <p>
     * Override this method to customize how {@code @CookieValue} parameter
     * validation errors are converted to Error objects.
     * </p>
     *
     * @param cookieValue the CookieValue annotation
     * @param result      the parameter validation result
     * @param errorList   the list to populate with errors
     */
    protected void handleCookieValue(CookieValue cookieValue, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.COOKIE,
                result.getMethodParameter().getParameterName(), errorList);
    }

    /**
     * Handles matrix variable validation errors.
     * <p>
     * Override this method to customize how {@code @MatrixVariable} parameter
     * validation errors are converted to Error objects.
     * </p>
     *
     * @param matrixVariable the MatrixVariable annotation
     * @param result         the parameter validation result
     * @param errorList      the list to populate with errors
     */
    protected void handleMatrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.PARAMETER,
                result.getMethodParameter().getParameterName(), errorList);
    }

    /**
     * Handles model attribute validation errors.
     * <p>
     * Override this method to customize how {@code @ModelAttribute} parameter
     * validation errors are converted to Error objects.
     * </p>
     *
     * @param modelAttribute the ModelAttribute annotation (may be null)
     * @param errors         the parameter errors
     * @param errorList      the list to populate with errors
     */
    protected void handleModelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors, List<Error> errorList) {
        errors.getAllErrors().stream()
                .map(this::objectErrorToError)
                .forEach(errorList::add);
    }

    /**
     * Handles path variable validation errors.
     * <p>
     * Override this method to customize how {@code @PathVariable} parameter
     * validation errors are converted to Error objects.
     * </p>
     *
     * @param pathVariable the PathVariable annotation
     * @param result       the parameter validation result
     * @param errorList    the list to populate with errors
     */
    protected void handlePathVariable(PathVariable pathVariable, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.PARAMETER,
                result.getMethodParameter().getParameterName(), errorList);
    }

    /**
     * Handles request body validation errors (ParameterErrors variant).
     * <p>
     * Override this method to customize how {@code @RequestBody} parameter
     * validation errors are converted to Error objects.
     * </p>
     *
     * @param requestBody the RequestBody annotation
     * @param errors      the parameter errors
     * @param errorList   the list to populate with errors
     */
    protected void handleRequestBody(RequestBody requestBody, ParameterErrors errors, List<Error> errorList) {
        errors.getAllErrors().stream()
                .map(this::objectErrorToError)
                .forEach(errorList::add);
    }

    /**
     * Handles request body validation errors (ParameterValidationResult variant).
     * <p>
     * Override this method to customize how {@code @RequestBody} validation
     * results are converted to Error objects.
     * </p>
     *
     * @param requestBody the RequestBody annotation
     * @param result      the parameter validation result
     * @param errorList   the list to populate with errors
     */
    protected void handleRequestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.PARAMETER, null, errorList);
    }

    /**
     * Handles request header validation errors.
     * <p>
     * Override this method to customize how {@code @RequestHeader} parameter
     * validation errors are converted to Error objects.
     * </p>
     *
     * @param requestHeader the RequestHeader annotation
     * @param result        the parameter validation result
     * @param errorList     the list to populate with errors
     */
    protected void handleRequestHeader(RequestHeader requestHeader, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.HEADER,
                result.getMethodParameter().getParameterName(), errorList);
    }

    /**
     * Handles request parameter validation errors.
     * <p>
     * Override this method to customize how {@code @RequestParam} parameter
     * validation errors are converted to Error objects.
     * </p>
     *
     * @param requestParam the RequestParam annotation (may be null)
     * @param result       the parameter validation result
     * @param errorList    the list to populate with errors
     */
    protected void handleRequestParam(@Nullable RequestParam requestParam, ParameterValidationResult result, List<Error> errorList) {
        addParameterValidationErrors(result, Error.Type.PARAMETER,
                result.getMethodParameter().getParameterName(), errorList);
    }

    /**
     * Handles request part validation errors.
     * <p>
     * Override this method to customize how {@code @RequestPart} parameter
     * validation errors are converted to Error objects.
     * </p>
     *
     * @param requestPart the RequestPart annotation
     * @param errors      the parameter errors
     * @param errorList   the list to populate with errors
     */
    protected void handleRequestPart(RequestPart requestPart, ParameterErrors errors, List<Error> errorList) {
        errors.getAllErrors().stream()
                .map(this::objectErrorToError)
                .forEach(errorList::add);
    }

    /**
     * Handles other parameter validation errors.
     * <p>
     * Override this method to customize how unsupported parameter types
     * are handled during validation error processing.
     * </p>
     *
     * @param result    the parameter validation result
     * @param errorList the list to populate with errors
     */
    protected void handleOther(ParameterValidationResult result, List<Error> errorList) {
        result.getResolvableErrors().forEach(error ->
                extendedProblemDetailLog.log(logger, null, "codes: {}, defaultMessage: {}",
                        error.getCodes(), error.getDefaultMessage()));
    }

    /**
     * Adds parameter validation errors to the error list.
     *
     * @param result        the parameter validation result
     * @param errorType     the error type
     * @param parameterName the parameter name
     * @param errorList     the list to populate with errors
     */
    protected void addParameterValidationErrors(ParameterValidationResult result, Error.Type errorType,
                                                @Nullable String parameterName, List<Error> errorList) {
        result.getResolvableErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .map(defaultMessage -> new Error(errorType, parameterName, defaultMessage))
                .forEach(errorList::add);
    }
}

package io.github.sbracely.extended.problem.detail.core.error.resolver;

import io.github.sbracely.extended.problem.detail.core.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.core.response.Error;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for resolving errors from exceptions and converting them to {@link Error} objects.
 * <p>
 * This class provides reusable logic for extracting error information from various Spring exceptions
 * into a unified {@link Error} representation. It is designed to be extended by
 * framework-specific implementations.
 * </p>
 * <p>
 * Subclasses should implement framework-specific exception resolving methods and delegate
 * to the protected methods in this class for common processing logic.
 * </p>
 * <p>
 * To customize error resolving, you can:
 * </p>
 * <ul>
 *     <li>Extend this class and override specific {@code resolveXxx} methods</li>
 *     <li>Create a custom resolver that delegates to this class</li>
 * </ul>
 *
 * @since 1.0.0
 */
public abstract class AbstractErrorResolver {

    private static final Log logger = LogFactory.getLog(AbstractErrorResolver.class);

    protected final ExtendedProblemDetailLog extendedProblemDetailLog;

    /**
     * Constructs a new resolver with the specified log instance.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     */
    protected AbstractErrorResolver(ExtendedProblemDetailLog extendedProblemDetailLog) {
        this.extendedProblemDetailLog = extendedProblemDetailLog;
    }

    /**
     * Resolves errors from {@link WebExchangeBindException}.
     *
     * @param ex the WebExchangeBindException to resolve
     * @return list of Error objects representing all errors
     */
    public List<Error> resolveWebExchangeBindException(WebExchangeBindException ex) {
        return resolveBindingResult(ex.getBindingResult());
    }

    /**
     * Resolves errors from {@link HandlerMethodValidationException} using the Visitor pattern.
     *
     * @param ex the HandlerMethodValidationException to resolve
     * @return list of Error objects representing all errors
     */
    public List<Error> resolveHandlerMethodValidationException(HandlerMethodValidationException ex) {
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
     * Resolves errors from {@link MethodValidationException}.
     *
     * @param ex the MethodValidationException to resolve
     * @return list of Error objects representing all errors
     */
    public List<Error> resolveMethodValidationException(MethodValidationException ex) {
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
     * @param ex            the HandlerMethodValidationException being processed
     * @param pathVariable  the PathVariable annotation
     * @param result        the parameter validation result
     * @param errorList     the list to populate with errors
     */
    protected void resolvePathVariable(HandlerMethodValidationException ex, PathVariable pathVariable, ParameterValidationResult result, List<Error> errorList) {
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
     * @param ex           the HandlerMethodValidationException being processed
     * @param requestBody  the RequestBody annotation
     * @param errors       the parameter errors
     * @param errorList    the list to populate with errors
     */
    protected void resolveRequestBody(HandlerMethodValidationException ex, RequestBody requestBody, ParameterErrors errors, List<Error> errorList) {
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
     * @param ex           the HandlerMethodValidationException being processed
     * @param requestBody  the RequestBody annotation
     * @param result       the parameter validation result
     * @param errorList    the list to populate with errors
     */
    protected void resolveRequestBodyValidationResult(HandlerMethodValidationException ex, RequestBody requestBody, ParameterValidationResult result, List<Error> errorList) {
        addParameterErrors(result, Error.Type.PARAMETER, null, errorList);
    }

    /**
     * Resolves errors from request header parameter.
     * <p>
     * Override this method to customize how {@code @RequestHeader} parameter
     * errors are converted to Error objects.
     * </p>
     *
     * @param ex             the HandlerMethodValidationException being processed
     * @param requestHeader  the RequestHeader annotation
     * @param result         the parameter validation result
     * @param errorList      the list to populate with errors
     */
    protected void resolveRequestHeader(HandlerMethodValidationException ex, RequestHeader requestHeader, ParameterValidationResult result, List<Error> errorList) {
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
     * @param ex            the HandlerMethodValidationException being processed
     * @param requestParam  the RequestParam annotation (may be null)
     * @param result        the parameter validation result
     * @param errorList     the list to populate with errors
     */
    protected void resolveRequestParam(HandlerMethodValidationException ex, @Nullable RequestParam requestParam, ParameterValidationResult result, List<Error> errorList) {
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
     * @param ex           the HandlerMethodValidationException being processed
     * @param requestPart  the RequestPart annotation
     * @param errors       the parameter errors
     * @param errorList    the list to populate with errors
     */
    protected void resolveRequestPart(HandlerMethodValidationException ex, RequestPart requestPart, ParameterErrors errors, List<Error> errorList) {
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
     * @param ex         the HandlerMethodValidationException being processed
     * @param result     the parameter validation result
     * @param errorList  the list to populate with errors
     */
    protected void resolveOther(HandlerMethodValidationException ex, ParameterValidationResult result, List<Error> errorList) {
        result.getResolvableErrors().forEach(error ->
                extendedProblemDetailLog.log(logger, null, "codes: {}, defaultMessage: {}",
                        error.getCodes(), error.getDefaultMessage()));
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
}

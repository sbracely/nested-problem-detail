package com.github.sbracely.extended.problem.detail.core.converter;

import com.github.sbracely.extended.problem.detail.core.response.Error;
import com.github.sbracely.extended.problem.detail.core.response.ExtendedProblemDetail;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for converting validation errors to {@link Error} objects.
 * <p>
 * This class provides static methods to process various types of validation exceptions
 * and convert them into a list of {@link Error} objects for use in extended problem detail responses.
 * It handles both simple parameter validation and complex method validation exceptions.
 * </p>
 *
 * @see Error
 * @see ExtendedProblemDetail
 * @since 0.0.1-SNAPSHOT
 */
public final class ErrorConverter {

    private static final Logger logger = LoggerFactory.getLogger(ErrorConverter.class);

    private ErrorConverter() {
    }

    /**
     * Converts an {@link ObjectError} to an {@link Error} object.
     * <p>
     * If the ObjectError is a {@link FieldError}, extracts the field name as the target.
     * Sets the error type to {@link Error.Type#PARAMETER} by default.
     * </p>
     *
     * @param objectError the ObjectError to convert
     * @return Error object with field and message information
     */
    public static Error objectErrorConvertToError(ObjectError objectError) {
        String target = null;
        if (objectError instanceof FieldError fieldError) {
            target = fieldError.getField();
        }
        return new Error(Error.Type.PARAMETER, target, objectError.getDefaultMessage());
    }

    /**
     * Converts a {@link MethodValidationException} to a list of {@link Error} objects.
     * <p>
     * Processes both parameter validation results and cross-parameter validation results.
     * For {@link ParameterErrors}, extracts all errors and converts them.
     * For other validation results, creates errors with parameter names.
     * </p>
     *
     * @param ex the MethodValidationException to convert
     * @return list of Error objects representing all validation errors
     */
    public static List<Error> methodValidationExceptionConvertToError(MethodValidationException ex) {
        List<Error> errors = new ArrayList<>();
        ex.getParameterValidationResults().forEach(parameterValidationResult -> {
            if (parameterValidationResult instanceof ParameterErrors parameterErrors) {
                parameterErrors.getAllErrors().stream()
                        .map(ErrorConverter::objectErrorConvertToError)
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
     * Processes a {@link HandlerMethodValidationException} using the Visitor pattern
     * and converts all validation errors to a list of {@link Error} objects.
     * <p>
     * This method visits each type of parameter validation result and converts them
     * into Error objects. It handles:
     * </p>
     * <ul>
     *     <li>Cookie values ({@link CookieValue})</li>
     *     <li>Matrix variables ({@link MatrixVariable})</li>
     *     <li>Model attributes ({@link ModelAttribute})</li>
     *     <li>Path variables ({@link PathVariable})</li>
     *     <li>Request bodies ({@link RequestBody})</li>
     *     <li>Request headers ({@link RequestHeader})</li>
     *     <li>Request parameters ({@link RequestParam})</li>
     *     <li>Request parts ({@link RequestPart})</li>
     * </ul>
     *
     * @param ex the HandlerMethodValidationException to process
     * @return list of Error objects representing all validation errors
     */
    public static List<Error> processHandlerMethodValidationException(HandlerMethodValidationException ex) {
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
                        logger.warn("codes: {}, defaultMessage: {}", error.getCodes(), error.getDefaultMessage()));
            }

            private @Nullable String getParameterName(ParameterValidationResult result) {
                return result.getMethodParameter().getParameterName();
            }

            private void processParameterValidationResult(ParameterValidationResult result,
                                                          Error.Type errorType,
                                                          @Nullable String parameterName) {
                result.getResolvableErrors().stream()
                        .map(MessageSourceResolvable::getDefaultMessage)
                        .map(defaultMessage -> new Error(errorType, parameterName, defaultMessage))
                        .forEach(errorList::add);
            }

            private void processParameterErrors(ParameterErrors errors) {
                errors.getAllErrors().stream()
                        .map(ErrorConverter::objectErrorConvertToError)
                        .forEach(errorList::add);
            }
        });
        return errorList;
    }
}

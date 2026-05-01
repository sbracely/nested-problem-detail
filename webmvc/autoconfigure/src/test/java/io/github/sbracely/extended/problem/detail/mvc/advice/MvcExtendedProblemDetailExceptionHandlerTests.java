package io.github.sbracely.extended.problem.detail.mvc.advice;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import org.apache.commons.logging.Log;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties.DEFAULT_ERRORS_PROPERTY_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MvcExtendedProblemDetailExceptionHandler}.
 *
 * @since 1.0.0
 */
class MvcExtendedProblemDetailExceptionHandlerTests {

    private MvcRecordingLog mockLogger;

    private MvcExtendedProblemDetailExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        mockLogger = new MvcRecordingLog();
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, false);
        handler = new MvcExtendedProblemDetailExceptionHandler(log, DEFAULT_ERRORS_PROPERTY_NAME);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        request.addPreferredLocale(Locale.ENGLISH);
        webRequest = new ServletWebRequest(request, new MockHttpServletResponse());
    }

    @Test
    void shouldHandleHandlerMethodValidationExceptionWithoutLogConfiguration() {
        MvcExtendedProblemDetailExceptionHandler handlerWithoutLog =
                new MvcExtendedProblemDetailExceptionHandler(null, DEFAULT_ERRORS_PROPERTY_NAME);
        HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("name", "must not be blank");

        ResponseEntity<Object> response = handlerWithoutLog.handleHandlerMethodValidationException(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        assertThat(errorsOf((ProblemDetail) response.getBody())).hasSize(1);
    }

    @Test
    void shouldUseCustomErrorsPropertyName() {
        MvcExtendedProblemDetailExceptionHandler customHandler =
                new MvcExtendedProblemDetailExceptionHandler(null, "violations");
        HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("name", "must not be blank");

        ResponseEntity<Object> response = customHandler.handleHandlerMethodValidationException(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
        ProblemDetail body = (ProblemDetail) response.getBody();
        assertThat(body.getProperties())
                .containsKey("violations")
                .doesNotContainKey("errors");
        assertThat(errorsOf(body, "violations")).hasSize(1);
    }

    // =====================================================================
    // handleMethodArgumentNotValid
    // =====================================================================

    @Nested
    class MvcHandleMethodArgumentNotValid {

        @Test
        void shouldReturnExtendedProblemDetailWithFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field1", "Field1 is required"));
            bindingResult.addError(new FieldError("testBean", "field2", "Field2 must be positive"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
            ProblemDetail body = (ProblemDetail) response.getBody();
            assertThat(body).isNotNull();
            assertThat(errorsOf(body)).hasSize(2);
            assertThat(errorsOf(body).get(0).target()).isEqualTo("field1");
            assertThat(errorsOf(body).get(0).message()).isEqualTo("Field1 is required");
            assertThat(errorsOf(body).get(1).target()).isEqualTo("field2");
            assertThat(errorsOf(body).get(1).message()).isEqualTo("Field2 must be positive");
        }

        @Test
        void shouldReturnExtendedProblemDetailWithObjectError() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new ObjectError("testBean", "Object error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            assertThat(response).isNotNull();
            ProblemDetail body = (ProblemDetail) response.getBody();
            assertThat(body).isNotNull();
            assertThat(errorsOf(body)).hasSize(1);
            assertThat(errorsOf(body).get(0).target()).isNull();
            assertThat(errorsOf(body).get(0).message()).isEqualTo("Object error");
        }

        @Test
        void shouldLogHandleMethodArgumentNotValid() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleMethodArgumentNotValid"), isNull());
        }
    }

    // =====================================================================
    // handleHandlerMethodValidationException
    // =====================================================================

    @Nested
    class MvcHandleHandlerMethodValidationException {

        @Test
        void shouldReturnExtendedProblemDetail() {
            HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("param", "must not be null");

            ResponseEntity<Object> response = handler.handleHandlerMethodValidationException(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
            ProblemDetail body = (ProblemDetail) response.getBody();
            assertThat(body).isNotNull();
            assertThat(errorsOf(body)).isNotEmpty();
        }

        @Test
        void shouldLogHandleHandlerMethodValidationException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("param", "must not be null");

            h.handleHandlerMethodValidationException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] handleHandlerMethodValidationException"), isNull());
            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveRequestParam"), isNull());
        }
    }

    // =====================================================================
    // handleMethodValidationException
    // =====================================================================

    @Nested
    class MvcHandleMethodValidationException {

        @Test
        void shouldReturnProblemDetailWithoutErrorsFromParameterErrors() {
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "name", "Name is required")));
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(List.of(parameterErrors), Collections.emptyList());

            ResponseEntity<Object> response = handler.handleMethodValidationException(
                    ex, new HttpHeaders(), HttpStatus.UNPROCESSABLE_CONTENT, webRequest);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
            assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
            ProblemDetail body = (ProblemDetail) response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getDetail()).isEqualTo("Validation failed");
        }

        @Test
        void shouldLogHandleMethodValidationException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "name", "Name is required")));
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(List.of(parameterErrors), Collections.emptyList());

            h.handleMethodValidationException(ex, new HttpHeaders(), HttpStatus.UNPROCESSABLE_CONTENT, webRequest);

            verify(mockLogger).debug(actual -> actual.contains("MethodValidationException validation errors: ")
                    && actual.contains("Name is required"), isNull());
        }
    }

    // =====================================================================
    // handleErrorResponseException (including WebExchangeBindException branch)
    // =====================================================================

    @Nested
    class MvcHandleErrorResponseException {

        @Test
        void shouldReturnExtendedProblemDetailForWebExchangeBindException() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "email", "Invalid email"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            ResponseEntity<Object> response = handler.handleErrorResponseException(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
            ProblemDetail body = (ProblemDetail) response.getBody();
            assertThat(body).isNotNull();
            assertThat(errorsOf(body)).hasSize(1);
            assertThat(errorsOf(body).get(0).target()).isEqualTo("email");
            assertThat(errorsOf(body).get(0).message()).isEqualTo("Invalid email");
        }

        @Test
        void shouldReturnNullBodyForNonWebExchangeBindException() {
            org.springframework.web.ErrorResponseException ex =
                    new org.springframework.web.ErrorResponseException(HttpStatus.CONFLICT);

            ResponseEntity<Object> response = handler.handleErrorResponseException(
                    ex, new HttpHeaders(), HttpStatus.CONFLICT, webRequest);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);
            assertThat(((ProblemDetail) response.getBody()).getProperties()).isNull();
        }

        @Test
        void shouldLogHandleErrorResponseException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            h.handleErrorResponseException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleErrorResponseException"), isNull());
        }
    }

    // =====================================================================
    // handleAsyncRequestNotUsableException
    // =====================================================================

    @Nested
    class MvcHandleAsyncRequestNotUsableException {

        @Test
        void shouldReturnNull() {
            AsyncRequestNotUsableException ex = new AsyncRequestNotUsableException("Async not usable");

            ResponseEntity<Object> response = handler.handleAsyncRequestNotUsableException(ex, webRequest);

            assertThat(response).isNull();
        }

        @Test
        void shouldLogHandleAsyncRequestNotUsableException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            AsyncRequestNotUsableException ex = new AsyncRequestNotUsableException("Async not usable");

            h.handleAsyncRequestNotUsableException(ex, webRequest);

            verify(mockLogger).debug(eq("handleAsyncRequestNotUsableException"), isNull());
        }
    }

    // =====================================================================
    // resolveMethodArgumentNotValidException
    // =====================================================================

    @Nested
    class MvcResolveMethodArgumentNotValidException {

        @Test
        void shouldResolveFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field", "Field error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            List<Error> errors = handler.resolveMethodArgumentNotValidException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isEqualTo("field");
            assertThat(errors.get(0).message()).isEqualTo("Field error");
        }
    }

    // =====================================================================
    // resolveWebExchangeBindException
    // =====================================================================

    @Nested
    class MvcResolveWebExchangeBindException {

        @Test
        void shouldResolveFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "name", "Name is required"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            List<Error> errors = handler.resolveWebExchangeBindException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isEqualTo("name");
            assertThat(errors.get(0).message()).isEqualTo("Name is required");
        }
    }

    // =====================================================================
    // resolveBindingResult
    // =====================================================================

    @Nested
    class MvcResolveBindingResult {

        @Test
        void shouldConvertFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "email", "Invalid email format"));

            List<Error> errors = handler.resolveBindingResult(bindingResult);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isEqualTo("email");
            assertThat(errors.get(0).message()).isEqualTo("Invalid email format");
        }

        @Test
        void shouldConvertMixedErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "email", "Invalid email format"));
            bindingResult.addError(new ObjectError("testBean", "Global error"));

            List<Error> errors = handler.resolveBindingResult(bindingResult);

            assertThat(errors).hasSize(2);
            assertThat(errors.get(0).target()).isEqualTo("email");
            assertThat(errors.get(1).target()).isNull();
            assertThat(errors.get(1).message()).isEqualTo("Global error");
        }

        @Test
        void shouldReturnEmptyListForNoErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");

            List<Error> errors = handler.resolveBindingResult(bindingResult);

            assertThat(errors).isEmpty();
        }
    }

    // =====================================================================
    // objectErrorToError
    // =====================================================================

    @Nested
    class MvcObjectErrorToError {

        @Test
        void shouldConvertObjectErrorWithNullTarget() {
            ObjectError objectError = new ObjectError("testBean", "Global error message");

            Error error = handler.objectErrorToError(objectError);

            assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(error.target()).isNull();
            assertThat(error.message()).isEqualTo("Global error message");
        }

        @Test
        void shouldConvertFieldErrorWithTarget() {
            FieldError fieldError = new FieldError("testBean", "username", "Username is too short");

            Error error = handler.objectErrorToError(fieldError);

            assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(error.target()).isEqualTo("username");
            assertThat(error.message()).isEqualTo("Username is too short");
        }
    }

    // =====================================================================
    // addParameterErrors
    // =====================================================================

    @Nested
    class MvcAddParameterErrors {

        @Test
        void shouldAddCookieTypeErrors() {
            ParameterValidationResult result = buildParameterValidationResult("cookie error");
            List<Error> errorList = new java.util.ArrayList<>();

            handler.addParameterErrors(result, Error.Type.COOKIE, "sessionId", errorList);

            assertThat(errorList).hasSize(1);
            assertThat(errorList.get(0).type()).isEqualTo(Error.Type.COOKIE);
            assertThat(errorList.get(0).target()).isEqualTo("sessionId");
            assertThat(errorList.get(0).message()).isEqualTo("cookie error");
        }

        @Test
        void shouldAddHeaderTypeErrors() {
            ParameterValidationResult result = buildParameterValidationResult("header error");
            List<Error> errorList = new java.util.ArrayList<>();

            handler.addParameterErrors(result, Error.Type.HEADER, "X-Auth", errorList);

            assertThat(errorList).hasSize(1);
            assertThat(errorList.get(0).type()).isEqualTo(Error.Type.HEADER);
            assertThat(errorList.get(0).target()).isEqualTo("X-Auth");
            assertThat(errorList.get(0).message()).isEqualTo("header error");
        }

        @Test
        void shouldAddParameterTypeWithNullName() {
            ParameterValidationResult result = buildParameterValidationResult("param error");
            List<Error> errorList = new java.util.ArrayList<>();

            handler.addParameterErrors(result, Error.Type.PARAMETER, null, errorList);

            assertThat(errorList).hasSize(1);
            assertThat(errorList.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errorList.get(0).target()).isNull();
            assertThat(errorList.get(0).message()).isEqualTo("param error");
        }

        @Test
        void shouldAddMultipleErrors() {
            ParameterValidationResult result = buildParameterValidationResult(List.of("error1", "error2"));
            List<Error> errorList = new java.util.ArrayList<>();

            handler.addParameterErrors(result, Error.Type.PARAMETER, "field", errorList);

            assertThat(errorList).hasSize(2);
            assertThat(errorList.get(0).message()).isEqualTo("error1");
            assertThat(errorList.get(1).message()).isEqualTo("error2");
        }
    }

    // =====================================================================
    // resolveMethodValidationException
    // =====================================================================

    @Nested
    class MvcResolveMethodValidationException {

        @Test
        void shouldResolveParameterErrors() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ParameterErrors parameterErrors = buildParameterErrors(List.of(
                    new FieldError("bean", "name", "required"),
                    new ObjectError("bean", "global error")
            ));
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(List.of(parameterErrors), Collections.emptyList());

            h.resolveMethodValidationException(ex);

            verify(mockLogger).debug(actual -> actual.contains("MethodValidationException validation errors: ")
                    && actual.contains("bean.name: required")
                    && actual.contains("bean: global error"), isNull());
        }

        @Test
        void shouldResolveRegularParameterValidationResult() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ParameterValidationResult pvr = buildParameterValidationResult("must be positive");
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(List.of(pvr), Collections.emptyList());

            h.resolveMethodValidationException(ex);

            verify(mockLogger).debug(actual -> actual.contains("MethodValidationException validation errors: ")
                    && actual.contains("bean: must be positive"), isNull());
        }

        @Test
        void shouldResolveCrossParameterValidationResults() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MessageSourceResolvable crossParamResolvable = messageSourceResolvable("cross param error");
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(Collections.emptyList(), List.of(crossParamResolvable));

            h.resolveMethodValidationException(ex);

            verify(mockLogger).debug(actual -> actual.contains("MethodValidationException validation errors: ")
                    && actual.contains("cross-parameter: cross param error"), isNull());
        }
    }

    // =====================================================================
    // Visitor branches (via resolveHandlerMethodValidationException)
    // =====================================================================

    @Nested
    class MvcResolveHandlerMethodValidationExceptionVisitors {

        @Test
        void shouldResolveCookieValue() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.cookieValue(annotation(CookieValue.class), buildParameterValidationResult("must not be blank")));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.COOKIE);
        }

        @Test
        void shouldLogResolveCookieValue() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.cookieValue(annotation(CookieValue.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveCookieValue"), isNull());
        }

        @Test
        void shouldResolveMatrixVariable() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.matrixVariable(annotation(MatrixVariable.class), buildParameterValidationResult("invalid")));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        }

        @Test
        void shouldLogResolveMatrixVariable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.matrixVariable(annotation(MatrixVariable.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveMatrixVariable"), isNull());
        }

        @Test
        void shouldResolveModelAttribute() {
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "field1", "must not be null")));
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.modelAttribute(null, parameterErrors));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isEqualTo("field1");
        }

        @Test
        void shouldLogResolveModelAttribute() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "f", "error")));
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.modelAttribute(null, parameterErrors));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveModelAttribute"), isNull());
        }

        @Test
        void shouldResolvePathVariable() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.pathVariable(annotation(PathVariable.class), buildParameterValidationResult("must be positive")));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        }

        @Test
        void shouldLogResolvePathVariable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.pathVariable(annotation(PathVariable.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolvePathVariable"), isNull());
        }

        @Test
        void shouldResolveRequestBody() {
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "bodyField", "invalid")));
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestBody(annotation(RequestBody.class), parameterErrors));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isEqualTo("bodyField");
        }

        @Test
        void shouldLogResolveRequestBody() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "f", "error")));
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestBody(annotation(RequestBody.class), parameterErrors));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveRequestBody"), isNull());
        }

        @Test
        void shouldResolveRequestBodyValidationResult() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestBodyValidationResult(annotation(RequestBody.class),
                            buildParameterValidationResult("must not be null")));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isNull();
        }

        @Test
        void shouldLogResolveRequestBodyValidationResult() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestBodyValidationResult(annotation(RequestBody.class),
                            buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveRequestBodyValidationResult"), isNull());
        }

        @Test
        void shouldResolveRequestHeader() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestHeader(annotation(RequestHeader.class), buildParameterValidationResult("must not be blank")));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.HEADER);
        }

        @Test
        void shouldLogResolveRequestHeader() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestHeader(annotation(RequestHeader.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveRequestHeader"), isNull());
        }

        @Test
        void shouldResolveRequestParam() {
            HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("name", "must not be null");

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        }

        @Test
        void shouldLogResolveRequestParam() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("name", "error");

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveRequestParam"), isNull());
        }

        @Test
        void shouldResolveRequestPart() {
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "file", "required")));
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestPart(annotation(RequestPart.class), parameterErrors));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isEqualTo("file");
        }

        @Test
        void shouldLogResolveRequestPart() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "f", "error")));
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestPart(annotation(RequestPart.class), parameterErrors));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveRequestPart"), isNull());
        }

        @Test
        void shouldResolveOtherWithNoErrorsAddedByDefault() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.other(buildParameterValidationResult("error")));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).isEmpty();
        }

        @Test
        void shouldLogResolveOther() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.other(buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveOther"), isNull());
        }
    }

    // =====================================================================
    // Handlers that log and delegate to super
    // =====================================================================

    @Nested
    class MvcDelegateToSuperHandlers {

        @Test
        void shouldLogHandleHttpRequestMethodNotSupported() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("GET");

            h.handleHttpRequestMethodNotSupported(ex, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED, webRequest);

            verify(mockLogger).debug(eq("handleHttpRequestMethodNotSupported"), isNull());
        }

        @Test
        void shouldLogHandleHttpMediaTypeNotSupported() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(
                    MediaType.TEXT_PLAIN, List.of(MediaType.APPLICATION_JSON));

            h.handleHttpMediaTypeNotSupported(ex, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, webRequest);

            verify(mockLogger).debug(eq("handleHttpMediaTypeNotSupported"), isNull());
        }

        @Test
        void shouldLogHandleHttpMediaTypeNotAcceptable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HttpMediaTypeNotAcceptableException ex = new HttpMediaTypeNotAcceptableException(
                    List.of(MediaType.APPLICATION_JSON));

            h.handleHttpMediaTypeNotAcceptable(ex, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, webRequest);

            verify(mockLogger).debug(eq("handleHttpMediaTypeNotAcceptable"), isNull());
        }

        @Test
        void shouldLogHandleMissingPathVariable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MissingPathVariableException ex = new MissingPathVariableException("id", createMethodParameter());

            h.handleMissingPathVariable(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

            verify(mockLogger).debug(eq("handleMissingPathVariable"), isNull());
        }

        @Test
        void shouldLogHandleMissingServletRequestParameter() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MissingServletRequestParameterException ex = new MissingServletRequestParameterException("name", "String");

            h.handleMissingServletRequestParameter(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleMissingServletRequestParameter"), isNull());
        }

        @Test
        void shouldLogHandleMissingServletRequestPart() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MissingServletRequestPartException ex = new MissingServletRequestPartException("file");

            h.handleMissingServletRequestPart(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleMissingServletRequestPart"), isNull());
        }

        @Test
        void shouldLogHandleServletRequestBindingException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ServletRequestBindingException ex = new ServletRequestBindingException("Missing header");

            h.handleServletRequestBindingException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleServletRequestBindingException"), isNull());
        }

        @Test
        void shouldLogHandleNoHandlerFoundException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/test", new HttpHeaders());

            h.handleNoHandlerFoundException(ex, new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);

            verify(mockLogger).debug(eq("handleNoHandlerFoundException"), isNull());
        }

        @Test
        void shouldLogHandleNoResourceFoundException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/static/missing.js", "missing.js");

            h.handleNoResourceFoundException(ex, new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);

            verify(mockLogger).debug(eq("handleNoResourceFoundException"), isNull());
        }

        @Test
        void shouldLogHandleAsyncRequestTimeoutException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            AsyncRequestTimeoutException ex = new AsyncRequestTimeoutException();

            h.handleAsyncRequestTimeoutException(ex, new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, webRequest);

            verify(mockLogger).debug(eq("handleAsyncRequestTimeoutException"), isNull());
        }

        @Test
        void shouldLogHandleMaxUploadSizeExceededException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1024L);

            h.handleMaxUploadSizeExceededException(ex, new HttpHeaders(), HttpStatus.CONTENT_TOO_LARGE, webRequest);

            verify(mockLogger).debug(eq("handleMaxUploadSizeExceededException"), isNull());
        }

        @Test
        void shouldLogHandleConversionNotSupported() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ConversionNotSupportedException ex = new ConversionNotSupportedException("value", Integer.class, null);

            h.handleConversionNotSupported(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

            verify(mockLogger).debug(eq("handleConversionNotSupported"), isNull());
        }

        @Test
        void shouldLogHandleTypeMismatch() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            TypeMismatchException ex = new TypeMismatchException("abc", Integer.class);

            h.handleTypeMismatch(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleTypeMismatch"), isNull());
        }

        @Test
        void shouldLogHandleHttpMessageNotReadable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                    "Not readable", new MockHttpInputMessage(new byte[0]));

            h.handleHttpMessageNotReadable(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleHttpMessageNotReadable"), isNull());
        }

        @Test
        void shouldLogHandleHttpMessageNotWritable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HttpMessageNotWritableException ex = new HttpMessageNotWritableException("Not writable");

            h.handleHttpMessageNotWritable(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

            verify(mockLogger).debug(eq("handleHttpMessageNotWritable"), isNull());
        }
    }

    // =====================================================================
    // Log level and printStackTrace behavior
    // =====================================================================

    @Nested
    class MvcLogBehavior {

        @Test
        void shouldLogWithExceptionWhenPrintStackTraceEnabled() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, true);
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleMethodArgumentNotValid"), eq(ex));
        }

        @Test
        void shouldLogHandlerMethodValidationExceptionWithSingleStackTraceWhenPrintStackTraceEnabled() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, true);
            HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("param", "must not be null");

            h.handleHandlerMethodValidationException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] handleHandlerMethodValidationException"), eq(ex));
            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveRequestParam"), isNull());
        }

        @Test
        void shouldNotLogWhenLevelIsOff() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.OFF, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verifyNoInteractions(mockLogger);
        }

        @Test
        void shouldLogAtInfoLevel() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.INFO, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).info(eq("handleMethodArgumentNotValid"), isNull());
        }

        @Test
        void shouldLogAtWarnLevel() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.WARN, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).warn(eq("handleMethodArgumentNotValid"), isNull());
        }

        @Test
        void shouldLogAtErrorLevel() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.ERROR, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).error(eq("handleMethodArgumentNotValid"), isNull());
        }

        @Test
        void shouldLogCorrelatedMessageWithoutStackTraceEvenIfPrintStackTraceEnabled() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, true);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.cookieValue(annotation(CookieValue.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveCookieValue"), isNull());
        }
    }

    // =====================================================================
    // Helper methods
    // =====================================================================

    private MvcExtendedProblemDetailExceptionHandlerWithMockLogger handlerWithMockLogger(
            LogLevel level, boolean printStackTrace) {
        return new MvcExtendedProblemDetailExceptionHandlerWithMockLogger(
                new ExtendedProblemDetailLog(level, printStackTrace), mockLogger);
    }

    @SuppressWarnings("unchecked")
    private static List<Error> errorsOf(ProblemDetail problemDetail) {
        return errorsOf(problemDetail, "errors");
    }

    @SuppressWarnings("unchecked")
    private static List<Error> errorsOf(ProblemDetail problemDetail, String errorsPropertyName) {
        return (List<Error>) problemDetail.getProperties().get(errorsPropertyName);
    }

    private MethodParameter createMethodParameter() {
        try {
            Method method = getClass().getMethod("testMethod", MvcTestBean.class);
            return new MethodParameter(method, 0);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void testMethod(MvcTestBean bean) {
        // used for reflection only
    }

    private ParameterValidationResult buildParameterValidationResult(String message) {
        return buildParameterValidationResult(List.of(message));
    }

    private ParameterValidationResult buildParameterValidationResult(List<String> messages) {
        return new ParameterValidationResult(
                createMethodParameter(),
                null,
                messages.stream().map(this::messageSourceResolvable).toList(),
                null,
                null,
                null,
                (error, sourceType) -> null);
    }

    private ParameterErrors buildParameterErrors(List<ObjectError> objectErrors) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new MvcTestBean(), "testBean");
        objectErrors.forEach(bindingResult::addError);
        return new ParameterErrors(createMethodParameter(), bindingResult.getTarget(), bindingResult, null, null, null);
    }

    private org.springframework.validation.method.MethodValidationException buildMethodValidationException(
            List<ParameterValidationResult> results,
            List<MessageSourceResolvable> crossResults) {
        MethodValidationResult mvr = MethodValidationResult.create(
                new MvcTestBean(),
                getTestMethod(),
                results,
                crossResults);
        return new org.springframework.validation.method.MethodValidationException(mvr);
    }

    private HandlerMethodValidationException buildExceptionVisiting(
            java.util.function.Consumer<HandlerMethodValidationException.Visitor> visitorConsumer) {
        ParameterValidationResult placeholder = buildParameterValidationResult("placeholder");
        MethodValidationResult mvr = MethodValidationResult.create(
                new MvcTestBean(), getTestMethod(), List.of(placeholder));
        return new HandlerMethodValidationException(mvr) {
            @Override
            public void visitResults(@NonNull Visitor visitor) {
                visitorConsumer.accept(visitor);
            }
        };
    }

    private HandlerMethodValidationException buildExceptionVisitingRequestParam(
            String paramName, String message) {
        ParameterValidationResult pvr = buildParameterValidationResult(message);
        return buildExceptionVisiting(visitor ->
                visitor.requestParam(annotation(RequestParam.class), pvr));
    }

    private MessageSourceResolvable messageSourceResolvable(String message) {
        return new DefaultMessageSourceResolvable(new String[0], message);
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> A annotation(Class<A> type) {
        return (A) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                    case "toString" -> type.getName();
                    case "hashCode" -> 0;
                    case "equals" -> proxy == args[0];
                    default -> null;
                };
            }
            if (method.getName().equals("annotationType")) {
                return type;
            }
            return method.getDefaultValue();
        });
    }

    private static MvcLogVerifier verify(MvcRecordingLog log) {
        return new MvcLogVerifier(log);
    }

    private static MvcMessageExpectation eq(String expected) {
        return actual -> actual.equals(expected);
    }

    private static MvcThrowableExpectation eq(Throwable expected) {
        return actual -> actual == expected;
    }

    private static MvcMessageExpectation matches(String regex) {
        return actual -> actual.matches(regex);
    }

    private static MvcThrowableExpectation isNull() {
        return actual -> actual == null;
    }

    private static void verifyNoInteractions(MvcRecordingLog log) {
        assertThat(log.events()).isEmpty();
    }

    private Method getTestMethod() {
        try {
            return getClass().getMethod("testMethod", MvcTestBean.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // =====================================================================
    // Inner classes
    // =====================================================================

    static class MvcExtendedProblemDetailExceptionHandlerWithMockLogger
            extends MvcExtendedProblemDetailExceptionHandler {

        MvcExtendedProblemDetailExceptionHandlerWithMockLogger(
                ExtendedProblemDetailLog extendedProblemDetailLog, Log mockLog) {
            super(extendedProblemDetailLog, DEFAULT_ERRORS_PROPERTY_NAME);
            injectLogger(mockLog);
        }

        private void injectLogger(Log mockLog) {
            try {
                java.lang.reflect.Field f =
                        MvcExtendedProblemDetailExceptionHandler.class.getDeclaredField("logger");
                f.setAccessible(true);
                f.set(this, mockLog);
            } catch (Exception e1) {
                try {
                    java.lang.reflect.Field f =
                            ResponseEntityExceptionHandler.class.getDeclaredField("logger");
                    f.setAccessible(true);
                    f.set(this, mockLog);
                } catch (Exception e2) {
                    throw new RuntimeException("Failed to inject mock logger", e2);
                }
            }
        }
    }

    @FunctionalInterface
    interface MvcMessageExpectation {
        boolean matches(String actual);
    }

    @FunctionalInterface
    interface MvcThrowableExpectation {
        boolean matches(Throwable actual);
    }

    record MvcLogEvent(String level, String message, Throwable throwable) {
    }

    static final class MvcLogVerifier {

        private final MvcRecordingLog log;

        MvcLogVerifier(MvcRecordingLog log) {
            this.log = log;
        }

        void trace(String message, Throwable throwable) {
            assertLogged("trace", actual -> actual.equals(message), actual -> actual == throwable);
        }

        void debug(String message, Throwable throwable) {
            assertLogged("debug", actual -> actual.equals(message), actual -> actual == throwable);
        }

        void debug(MvcMessageExpectation message, MvcThrowableExpectation throwable) {
            assertLogged("debug", message::matches, throwable::matches);
        }

        void info(String message, Throwable throwable) {
            assertLogged("info", actual -> actual.equals(message), actual -> actual == throwable);
        }

        void info(MvcMessageExpectation message, MvcThrowableExpectation throwable) {
            assertLogged("info", message::matches, throwable::matches);
        }

        void warn(String message, Throwable throwable) {
            assertLogged("warn", actual -> actual.equals(message), actual -> actual == throwable);
        }

        void warn(MvcMessageExpectation message, MvcThrowableExpectation throwable) {
            assertLogged("warn", message::matches, throwable::matches);
        }

        void error(String message, Throwable throwable) {
            assertLogged("error", actual -> actual.equals(message), actual -> actual == throwable);
        }

        void error(MvcMessageExpectation message, MvcThrowableExpectation throwable) {
            assertLogged("error", message::matches, throwable::matches);
        }

        private void assertLogged(String level, MvcMessageExpectation message, MvcThrowableExpectation throwable) {
            boolean found = log.events().stream()
                    .anyMatch(event -> event.level().equals(level)
                            && message.matches(event.message())
                            && throwable.matches(event.throwable()));
            assertThat(found).isTrue();
        }
    }

    static final class MvcRecordingLog implements Log {

        private final List<MvcLogEvent> events = new ArrayList<>();

        List<MvcLogEvent> events() {
            return events;
        }

        private void add(String level, Object message, Throwable throwable) {
            events.add(new MvcLogEvent(level, String.valueOf(message), throwable));
        }

        @Override
        public boolean isDebugEnabled() {
            return true;
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public boolean isFatalEnabled() {
            return true;
        }

        @Override
        public boolean isInfoEnabled() {
            return true;
        }

        @Override
        public boolean isTraceEnabled() {
            return true;
        }

        @Override
        public boolean isWarnEnabled() {
            return true;
        }

        @Override
        public void trace(Object message) {
            add("trace", message, null);
        }

        @Override
        public void trace(Object message, Throwable t) {
            add("trace", message, t);
        }

        @Override
        public void debug(Object message) {
            add("debug", message, null);
        }

        @Override
        public void debug(Object message, Throwable t) {
            add("debug", message, t);
        }

        @Override
        public void info(Object message) {
            add("info", message, null);
        }

        @Override
        public void info(Object message, Throwable t) {
            add("info", message, t);
        }

        @Override
        public void warn(Object message) {
            add("warn", message, null);
        }

        @Override
        public void warn(Object message, Throwable t) {
            add("warn", message, t);
        }

        @Override
        public void error(Object message) {
            add("error", message, null);
        }

        @Override
        public void error(Object message, Throwable t) {
            add("error", message, t);
        }

        @Override
        public void fatal(Object message) {
            add("fatal", message, null);
        }

        @Override
        public void fatal(Object message, Throwable t) {
            add("fatal", message, t);
        }
    }

    public static class MvcTestBean {
        private String field1;
        private String field2;
        private String name;
        private String email;
        private String username;
        private String field;

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}

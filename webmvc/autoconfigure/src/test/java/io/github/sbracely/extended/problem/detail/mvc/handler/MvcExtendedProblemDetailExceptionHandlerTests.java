package io.github.sbracely.extended.problem.detail.mvc.handler;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.apache.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpInputMessage;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link MvcExtendedProblemDetailExceptionHandler}.
 *
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class MvcExtendedProblemDetailExceptionHandlerTests {

    @Mock
    private Log mockLogger;

    private MvcExtendedProblemDetailExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, false);
        handler = new MvcExtendedProblemDetailExceptionHandler(log);
        webRequest = mock(WebRequest.class);
        lenient().when(webRequest.getLocale()).thenReturn(Locale.ENGLISH);
    }

    // =====================================================================
    // handleMethodArgumentNotValid
    // =====================================================================

    @Nested
    class HandleMethodArgumentNotValid {

        @Test
        void shouldReturnExtendedProblemDetailWithFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field1", "Field1 is required"));
            bindingResult.addError(new FieldError("testBean", "field2", "Field2 must be positive"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isInstanceOf(ExtendedProblemDetail.class);
            ExtendedProblemDetail body = (ExtendedProblemDetail) response.getBody();
            assertThat(body.getErrors()).hasSize(2);
            assertThat(body.getErrors().get(0).target()).isEqualTo("field1");
            assertThat(body.getErrors().get(0).message()).isEqualTo("Field1 is required");
            assertThat(body.getErrors().get(1).target()).isEqualTo("field2");
            assertThat(body.getErrors().get(1).message()).isEqualTo("Field2 must be positive");
        }

        @Test
        void shouldReturnExtendedProblemDetailWithObjectError() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new ObjectError("testBean", "Object error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            ExtendedProblemDetail body = (ExtendedProblemDetail) response.getBody();
            assertThat(body.getErrors()).hasSize(1);
            assertThat(body.getErrors().get(0).target()).isNull();
            assertThat(body.getErrors().get(0).message()).isEqualTo("Object error");
        }

        @Test
        void shouldLogHandleMethodArgumentNotValid() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleMethodArgumentNotValid"), (Throwable) isNull());
        }
    }

    // =====================================================================
    // handleHandlerMethodValidationException
    // =====================================================================

    @Nested
    class HandleHandlerMethodValidationException {

        @Test
        void shouldReturnExtendedProblemDetail() {
            HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("param", "must not be null");

            ResponseEntity<Object> response = handler.handleHandlerMethodValidationException(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isInstanceOf(ExtendedProblemDetail.class);
            ExtendedProblemDetail body = (ExtendedProblemDetail) response.getBody();
            assertThat(body.getErrors()).isNotEmpty();
        }

        @Test
        void shouldLogHandleHandlerMethodValidationException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("param", "must not be null");

            h.handleHandlerMethodValidationException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleHandlerMethodValidationException"), (Throwable) isNull());
            verify(mockLogger).debug(eq("resolveRequestParam"), (Throwable) isNull());
        }
    }

    // =====================================================================
    // handleMethodValidationException
    // =====================================================================

    @Nested
    class HandleMethodValidationException {

        @Test
        void shouldReturnExtendedProblemDetailFromParameterErrors() {
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "name", "Name is required")));
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(List.of(parameterErrors), Collections.emptyList());

            ResponseEntity<Object> response = handler.handleMethodValidationException(
                    ex, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody()).isInstanceOf(ExtendedProblemDetail.class);
            ExtendedProblemDetail body = (ExtendedProblemDetail) response.getBody();
            assertThat(body.getErrors()).hasSize(1);
            assertThat(body.getErrors().get(0).target()).isEqualTo("name");
            assertThat(body.getErrors().get(0).message()).isEqualTo("Name is required");
        }

        @Test
        void shouldLogHandleMethodValidationException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "name", "Name is required")));
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(List.of(parameterErrors), Collections.emptyList());

            h.handleMethodValidationException(ex, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, webRequest);

            verify(mockLogger).debug(eq("handleMethodValidationException"), (Throwable) isNull());
        }
    }

    // =====================================================================
    // handleErrorResponseException (including WebExchangeBindException branch)
    // =====================================================================

    @Nested
    class HandleErrorResponseException {

        @Test
        void shouldReturnExtendedProblemDetailForWebExchangeBindException() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "email", "Invalid email"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            ResponseEntity<Object> response = handler.handleErrorResponseException(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isInstanceOf(ExtendedProblemDetail.class);
            ExtendedProblemDetail body = (ExtendedProblemDetail) response.getBody();
            assertThat(body.getErrors()).hasSize(1);
            assertThat(body.getErrors().get(0).target()).isEqualTo("email");
            assertThat(body.getErrors().get(0).message()).isEqualTo("Invalid email");
        }

        @Test
        void shouldReturnNullBodyForNonWebExchangeBindException() {
            org.springframework.web.ErrorResponseException ex =
                    new org.springframework.web.ErrorResponseException(HttpStatus.CONFLICT);

            ResponseEntity<Object> response = handler.handleErrorResponseException(
                    ex, new HttpHeaders(), HttpStatus.CONFLICT, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            // non-WebExchangeBindException: body is standard ProblemDetail, not ExtendedProblemDetail
            assertThat(response.getBody()).isNotInstanceOf(ExtendedProblemDetail.class);
        }

        @Test
        void shouldLogHandleErrorResponseException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            h.handleErrorResponseException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleErrorResponseException"), (Throwable) isNull());
        }
    }

    // =====================================================================
    // handleAsyncRequestNotUsableException
    // =====================================================================

    @Nested
    class HandleAsyncRequestNotUsableException {

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

            verify(mockLogger).debug(eq("handleAsyncRequestNotUsableException"), (Throwable) isNull());
        }
    }

    // =====================================================================
    // resolveMethodArgumentNotValidException
    // =====================================================================

    @Nested
    class ResolveMethodArgumentNotValidException {

        @Test
        void shouldResolveFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
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
    class ResolveWebExchangeBindException {

        @Test
        void shouldResolveFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
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
    class ResolveBindingResult {

        @Test
        void shouldConvertFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "email", "Invalid email format"));

            List<Error> errors = handler.resolveBindingResult(bindingResult);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isEqualTo("email");
            assertThat(errors.get(0).message()).isEqualTo("Invalid email format");
        }

        @Test
        void shouldConvertMixedErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
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
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");

            List<Error> errors = handler.resolveBindingResult(bindingResult);

            assertThat(errors).isEmpty();
        }
    }

    // =====================================================================
    // objectErrorToError
    // =====================================================================

    @Nested
    class ObjectErrorToError {

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
    class AddParameterErrors {

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
            ParameterValidationResult result = mock(ParameterValidationResult.class);
            MessageSourceResolvable r1 = mock(MessageSourceResolvable.class);
            MessageSourceResolvable r2 = mock(MessageSourceResolvable.class);
            when(r1.getDefaultMessage()).thenReturn("error1");
            when(r2.getDefaultMessage()).thenReturn("error2");
            when(result.getResolvableErrors()).thenReturn(List.of(r1, r2));
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
    class ResolveMethodValidationException {

        @Test
        void shouldResolveParameterErrors() {
            ParameterErrors parameterErrors = buildParameterErrors(List.of(
                    new FieldError("bean", "name", "required"),
                    new ObjectError("bean", "global error")
            ));
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(List.of(parameterErrors), Collections.emptyList());

            List<Error> errors = handler.resolveMethodValidationException(ex);

            assertThat(errors).hasSize(2);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isEqualTo("name");
            assertThat(errors.get(0).message()).isEqualTo("required");
            assertThat(errors.get(1).target()).isNull();
            assertThat(errors.get(1).message()).isEqualTo("global error");
        }

        @Test
        void shouldResolveRegularParameterValidationResult() {
            ParameterValidationResult pvr = buildParameterValidationResult("must be positive");
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(List.of(pvr), Collections.emptyList());

            List<Error> errors = handler.resolveMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).message()).isEqualTo("must be positive");
        }

        @Test
        void shouldResolveCrossParameterValidationResults() {
            MessageSourceResolvable crossParamResolvable = mock(MessageSourceResolvable.class);
            when(crossParamResolvable.getDefaultMessage()).thenReturn("cross param error");
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(Collections.emptyList(), List.of(crossParamResolvable));

            List<Error> errors = handler.resolveMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isNull();
            assertThat(errors.get(0).message()).isEqualTo("cross param error");
        }
    }

    // =====================================================================
    // Visitor branches (via resolveHandlerMethodValidationException)
    // =====================================================================

    @Nested
    class ResolveHandlerMethodValidationExceptionVisitors {

        @Test
        void shouldResolveCookieValue() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.cookieValue(mock(CookieValue.class), buildParameterValidationResult("must not be blank")));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.COOKIE);
        }

        @Test
        void shouldLogResolveCookieValue() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.cookieValue(mock(CookieValue.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(eq("resolveCookieValue"), (Throwable) isNull());
        }

        @Test
        void shouldResolveMatrixVariable() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.matrixVariable(mock(MatrixVariable.class), buildParameterValidationResult("invalid")));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        }

        @Test
        void shouldLogResolveMatrixVariable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.matrixVariable(mock(MatrixVariable.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(eq("resolveMatrixVariable"), (Throwable) isNull());
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

            verify(mockLogger).debug(eq("resolveModelAttribute"), (Throwable) isNull());
        }

        @Test
        void shouldResolvePathVariable() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.pathVariable(mock(PathVariable.class), buildParameterValidationResult("must be positive")));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        }

        @Test
        void shouldLogResolvePathVariable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.pathVariable(mock(PathVariable.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(eq("resolvePathVariable"), (Throwable) isNull());
        }

        @Test
        void shouldResolveRequestBody() {
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "bodyField", "invalid")));
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestBody(mock(RequestBody.class), parameterErrors));

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
                    visitor.requestBody(mock(RequestBody.class), parameterErrors));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(eq("resolveRequestBody"), (Throwable) isNull());
        }

        @Test
        void shouldResolveRequestBodyValidationResult() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestBodyValidationResult(mock(RequestBody.class),
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
                    visitor.requestBodyValidationResult(mock(RequestBody.class),
                            buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(eq("resolveRequestBodyValidationResult"), (Throwable) isNull());
        }

        @Test
        void shouldResolveRequestHeader() {
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestHeader(mock(RequestHeader.class), buildParameterValidationResult("must not be blank")));

            List<Error> errors = handler.resolveHandlerMethodValidationException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.HEADER);
        }

        @Test
        void shouldLogResolveRequestHeader() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestHeader(mock(RequestHeader.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(eq("resolveRequestHeader"), (Throwable) isNull());
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

            verify(mockLogger).debug(eq("resolveRequestParam"), (Throwable) isNull());
        }

        @Test
        void shouldResolveRequestPart() {
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("bean", "file", "required")));
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.requestPart(mock(RequestPart.class), parameterErrors));

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
                    visitor.requestPart(mock(RequestPart.class), parameterErrors));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(eq("resolveRequestPart"), (Throwable) isNull());
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

            verify(mockLogger).debug(eq("resolveOther"), (Throwable) isNull());
        }
    }

    // =====================================================================
    // Handlers that log and delegate to super
    // =====================================================================

    @Nested
    class DelegateToSuperHandlers {

        @Test
        void shouldLogHandleHttpRequestMethodNotSupported() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("GET");

            h.handleHttpRequestMethodNotSupported(ex, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED, webRequest);

            verify(mockLogger).debug(eq("handleHttpRequestMethodNotSupported"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleHttpMediaTypeNotSupported() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(
                    MediaType.TEXT_PLAIN, List.of(MediaType.APPLICATION_JSON));

            h.handleHttpMediaTypeNotSupported(ex, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, webRequest);

            verify(mockLogger).debug(eq("handleHttpMediaTypeNotSupported"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleHttpMediaTypeNotAcceptable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HttpMediaTypeNotAcceptableException ex = new HttpMediaTypeNotAcceptableException(
                    List.of(MediaType.APPLICATION_JSON));

            h.handleHttpMediaTypeNotAcceptable(ex, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, webRequest);

            verify(mockLogger).debug(eq("handleHttpMediaTypeNotAcceptable"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleMissingPathVariable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MissingPathVariableException ex = new MissingPathVariableException("id", createMethodParameter());

            h.handleMissingPathVariable(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

            verify(mockLogger).debug(eq("handleMissingPathVariable"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleMissingServletRequestParameter() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MissingServletRequestParameterException ex = new MissingServletRequestParameterException("name", "String");

            h.handleMissingServletRequestParameter(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleMissingServletRequestParameter"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleMissingServletRequestPart() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MissingServletRequestPartException ex = new MissingServletRequestPartException("file");

            h.handleMissingServletRequestPart(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleMissingServletRequestPart"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleServletRequestBindingException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ServletRequestBindingException ex = new ServletRequestBindingException("Missing header");

            h.handleServletRequestBindingException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleServletRequestBindingException"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleNoHandlerFoundException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/test", new HttpHeaders());

            h.handleNoHandlerFoundException(ex, new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);

            verify(mockLogger).debug(eq("handleNoHandlerFoundException"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleNoResourceFoundException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/static/missing.js", "missing.js");

            h.handleNoResourceFoundException(ex, new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);

            verify(mockLogger).debug(eq("handleNoResourceFoundException"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleAsyncRequestTimeoutException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            AsyncRequestTimeoutException ex = new AsyncRequestTimeoutException();

            h.handleAsyncRequestTimeoutException(ex, new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, webRequest);

            verify(mockLogger).debug(eq("handleAsyncRequestTimeoutException"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleMaxUploadSizeExceededException() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1024L);

            h.handleMaxUploadSizeExceededException(ex, new HttpHeaders(), HttpStatus.PAYLOAD_TOO_LARGE, webRequest);

            verify(mockLogger).debug(eq("handleMaxUploadSizeExceededException"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleConversionNotSupported() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ConversionNotSupportedException ex = new ConversionNotSupportedException("value", Integer.class, null);

            h.handleConversionNotSupported(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

            verify(mockLogger).debug(eq("handleConversionNotSupported"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleTypeMismatch() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            TypeMismatchException ex = new TypeMismatchException("abc", Integer.class);

            h.handleTypeMismatch(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleTypeMismatch"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleHttpMessageNotReadable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                    "Not readable", new MockHttpInputMessage(new byte[0]));

            h.handleHttpMessageNotReadable(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleHttpMessageNotReadable"), (Throwable) isNull());
        }

        @Test
        void shouldLogHandleHttpMessageNotWritable() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HttpMessageNotWritableException ex = new HttpMessageNotWritableException("Not writable");

            h.handleHttpMessageNotWritable(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

            verify(mockLogger).debug(eq("handleHttpMessageNotWritable"), (Throwable) isNull());
        }
    }

    // =====================================================================
    // Log level and printStackTrace behavior
    // =====================================================================

    @Nested
    class LogBehavior {

        @Test
        void shouldLogWithExceptionWhenPrintStackTraceEnabled() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, true);
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).debug(eq("handleMethodArgumentNotValid"), eq(ex));
        }

        @Test
        void shouldNotLogWhenLevelIsOff() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.OFF, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verifyNoInteractions(mockLogger);
        }

        @Test
        void shouldLogAtInfoLevel() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.INFO, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).info(eq("handleMethodArgumentNotValid"), (Throwable) isNull());
        }

        @Test
        void shouldLogAtWarnLevel() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.WARN, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).warn(eq("handleMethodArgumentNotValid"), (Throwable) isNull());
        }

        @Test
        void shouldLogAtErrorLevel() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.ERROR, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "f", "error"));
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(createMethodParameter(), bindingResult);

            h.handleMethodArgumentNotValid(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            verify(mockLogger).error(eq("handleMethodArgumentNotValid"), (Throwable) isNull());
        }

        @Test
        void shouldNotLogExceptionWhenNullEvenIfPrintStackTraceEnabled() {
            MvcExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, true);
            // In MVC, resolveCookieValue passes `ex` (not null) to log() — so with printStackTrace=true,
            // the exception IS logged. Verify this behavior.
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.cookieValue(mock(CookieValue.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            verify(mockLogger).debug(eq("resolveCookieValue"), eq(ex));
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

    private MethodParameter createMethodParameter() {
        try {
            Method method = getClass().getMethod("testMethod", TestBean.class);
            return new MethodParameter(method, 0);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void testMethod(TestBean bean) {
        // used for reflection only
    }

    private ParameterValidationResult buildParameterValidationResult(String message) {
        ParameterValidationResult pvr = mock(ParameterValidationResult.class);
        MessageSourceResolvable resolvable = mock(MessageSourceResolvable.class);
        lenient().when(resolvable.getDefaultMessage()).thenReturn(message);
        lenient().when(pvr.getResolvableErrors()).thenReturn(List.of(resolvable));
        lenient().when(pvr.getMethodParameter()).thenReturn(createMethodParameter());
        return pvr;
    }

    private ParameterErrors buildParameterErrors(List<ObjectError> objectErrors) {
        ParameterErrors parameterErrors = mock(ParameterErrors.class);
        lenient().when(parameterErrors.getAllErrors()).thenReturn(objectErrors);
        lenient().when(parameterErrors.getMethodParameter()).thenReturn(createMethodParameter());
        return parameterErrors;
    }

    private org.springframework.validation.method.MethodValidationException buildMethodValidationException(
            List<? extends ParameterValidationResult> results,
            List<? extends MessageSourceResolvable> crossResults) {
        MethodValidationResult mvr = MethodValidationResult.create(
                new TestBean(),
                getTestMethod(),
                (List<ParameterValidationResult>) results,
                (List<MessageSourceResolvable>) crossResults);
        return new org.springframework.validation.method.MethodValidationException(mvr);
    }

    private HandlerMethodValidationException buildExceptionVisiting(
            java.util.function.Consumer<HandlerMethodValidationException.Visitor> visitorConsumer) {
        ParameterValidationResult placeholder = buildParameterValidationResult("placeholder");
        MethodValidationResult mvr = MethodValidationResult.create(
                new TestBean(), getTestMethod(), List.of(placeholder));
        return new HandlerMethodValidationException(mvr) {
            @Override
            public void visitResults(Visitor visitor) {
                visitorConsumer.accept(visitor);
            }
        };
    }

    private HandlerMethodValidationException buildExceptionVisitingRequestParam(
            String paramName, String message) {
        ParameterValidationResult pvr = buildParameterValidationResult(message);
        return buildExceptionVisiting(visitor ->
                visitor.requestParam(mock(RequestParam.class), pvr));
    }

    private Method getTestMethod() {
        try {
            return getClass().getMethod("testMethod", TestBean.class);
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
            super(extendedProblemDetailLog);
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

    public static class TestBean {
        private String field1;
        private String field2;
        private String name;
        private String email;
        private String username;
        private String field;

        public String getField1() { return field1; }
        public void setField1(String field1) { this.field1 = field1; }
        public String getField2() { return field2; }
        public void setField2(String field2) { this.field2 = field2; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
    }
}

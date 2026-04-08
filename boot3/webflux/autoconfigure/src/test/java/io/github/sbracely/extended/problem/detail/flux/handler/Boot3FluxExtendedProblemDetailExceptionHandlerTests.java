package io.github.sbracely.extended.problem.detail.flux.handler;

import io.github.sbracely.extended.problem.detail.common.logging.Boot3CommonExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.apache.commons.logging.Log;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Boot3FluxExtendedProblemDetailExceptionHandler}.
 *
 * @since 1.0.0
 */
class Boot3FluxExtendedProblemDetailExceptionHandlerTests {

    private Boot3FluxRecordingLog mockLogger;

    private Boot3FluxExtendedProblemDetailExceptionHandler handler;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        mockLogger = new Boot3FluxRecordingLog();
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.DEBUG, false);
        handler = new Boot3FluxExtendedProblemDetailExceptionHandler(log);
        exchange = mockServerWebExchange();
    }

    // =====================================================================
    // handleWebExchangeBindException
    // =====================================================================

    @Nested
    class Boot3FluxHandleWebExchangeBindException {

        @Test
        void shouldReturnExtendedProblemDetailWithFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field1", "Field1 is required"));
            bindingResult.addError(new FieldError("testBean", "field2", "Field2 must be positive"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            Mono<ResponseEntity<Object>> result = handler.handleWebExchangeBindException(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange);

            StepVerifier.create(result)
                    .assertNext(responseEntity -> {
                        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(responseEntity.getBody()).isInstanceOf(ExtendedProblemDetail.class);
                        ExtendedProblemDetail body = (ExtendedProblemDetail) responseEntity.getBody();
                        assertThat(body).isNotNull();
                        assertThat(body.getErrors()).hasSize(2);
                        assertThat(body.getErrors().get(0).target()).isEqualTo("field1");
                        assertThat(body.getErrors().get(0).message()).isEqualTo("Field1 is required");
                        assertThat(body.getErrors().get(1).target()).isEqualTo("field2");
                        assertThat(body.getErrors().get(1).message()).isEqualTo("Field2 must be positive");
                    })
                    .verifyComplete();
        }

        @Test
        void shouldReturnExtendedProblemDetailWithObjectError() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new ObjectError("testBean", "Object error message"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            Mono<ResponseEntity<Object>> result = handler.handleWebExchangeBindException(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange);

            StepVerifier.create(result)
                    .assertNext(responseEntity -> {
                        ExtendedProblemDetail body = (ExtendedProblemDetail) responseEntity.getBody();
                        assertThat(body).isNotNull();
                        assertThat(body.getErrors()).hasSize(1);
                        assertThat(body.getErrors().get(0).target()).isNull();
                        assertThat(body.getErrors().get(0).message()).isEqualTo("Object error message");
                    })
                    .verifyComplete();
        }

        @Test
        void shouldLogHandleWebExchangeBindException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field1", "error"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            h.handleWebExchangeBindException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleWebExchangeBindException"), isNull());
        }
    }

    // =====================================================================
    // handleHandlerMethodValidationException
    // =====================================================================

    @Nested
    class Boot3FluxHandleHandlerMethodValidationException {

        @Test
        void shouldReturnExtendedProblemDetail() {
            HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("param", "must not be null");

            Mono<ResponseEntity<Object>> result = handler.handleHandlerMethodValidationException(
                    ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange);

            StepVerifier.create(result)
                    .assertNext(responseEntity -> {
                        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(responseEntity.getBody()).isInstanceOf(ExtendedProblemDetail.class);
                        ExtendedProblemDetail body = (ExtendedProblemDetail) responseEntity.getBody();
                        assertThat(body).isNotNull();
                        assertThat(body.getErrors()).isNotEmpty();
                    })
                    .verifyComplete();
        }

        @Test
        void shouldLogHandleHandlerMethodValidationException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            HandlerMethodValidationException ex = buildExceptionVisitingRequestParam("param", "must not be null");

            h.handleHandlerMethodValidationException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange)
                    .block();

            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] handleHandlerMethodValidationException"), isNull());
            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveRequestParam"), isNull());
        }
    }

    // =====================================================================
    // handleMethodValidationException
    // =====================================================================

    @Nested
    class Boot3FluxHandleMethodValidationException {

        @Test
        void shouldReturnExtendedProblemDetailFromParameterErrors() {
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new FieldError("testBean", "name", "Name is required")));
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(List.of(parameterErrors), Collections.emptyList());

            Mono<ResponseEntity<Object>> result = handler.handleMethodValidationException(
                    ex, HttpStatus.UNPROCESSABLE_ENTITY, exchange);

            StepVerifier.create(result)
                    .assertNext(responseEntity -> {
                        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                        assertThat(responseEntity.getBody()).isInstanceOf(ExtendedProblemDetail.class);
                        ExtendedProblemDetail body = (ExtendedProblemDetail) responseEntity.getBody();
                        assertThat(body).isNotNull();
                        assertThat(body.getErrors()).isNotEmpty();
                        assertThat(body.getErrors()).hasSize(1);
                        assertThat(body.getErrors().get(0).target()).isEqualTo("name");
                        assertThat(body.getErrors().get(0).message()).isEqualTo("Name is required");
                    })
                    .verifyComplete();
        }

        @Test
        void shouldLogHandleMethodValidationException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ParameterErrors parameterErrors = buildParameterErrors(
                    List.of(new ObjectError("testBean", "error")));
            org.springframework.validation.method.MethodValidationException ex =
                    buildMethodValidationException(List.of(parameterErrors), Collections.emptyList());

            h.handleMethodValidationException(ex, HttpStatus.UNPROCESSABLE_ENTITY, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleMethodValidationException"), isNull());
        }
    }

    // =====================================================================
    // resolveMethodValidationException
    // =====================================================================

    @Nested
    class Boot3FluxResolveMethodValidationException {

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
            MessageSourceResolvable crossParamResolvable = messageSourceResolvable("cross param error");
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
    // resolveWebExchangeBindException
    // =====================================================================

    @Nested
    class Boot3FluxResolveWebExchangeBindException {

        @Test
        void shouldResolveFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field", "Field error"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            List<Error> errors = handler.resolveWebExchangeBindException(ex);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isEqualTo("field");
            assertThat(errors.get(0).message()).isEqualTo("Field error");
        }
    }

    // =====================================================================
    // resolveBindingResult
    // =====================================================================

    @Nested
    class Boot3FluxResolveBindingResult {

        @Test
        void shouldConvertFieldErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "name", "Name is required"));

            List<Error> errors = handler.resolveBindingResult(bindingResult);

            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(errors.get(0).target()).isEqualTo("name");
            assertThat(errors.get(0).message()).isEqualTo("Name is required");
        }

        @Test
        void shouldConvertMixedErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "email", "Invalid email format"));
            bindingResult.addError(new ObjectError("testBean", "Global error"));

            List<Error> errors = handler.resolveBindingResult(bindingResult);

            assertThat(errors).hasSize(2);
            assertThat(errors.get(0).target()).isEqualTo("email");
            assertThat(errors.get(0).message()).isEqualTo("Invalid email format");
            assertThat(errors.get(1).target()).isNull();
            assertThat(errors.get(1).message()).isEqualTo("Global error");
        }

        @Test
        void shouldReturnEmptyListForNoErrors() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");

            List<Error> errors = handler.resolveBindingResult(bindingResult);

            assertThat(errors).isEmpty();
        }
    }

    // =====================================================================
    // objectErrorToError
    // =====================================================================

    @Nested
    class Boot3FluxObjectErrorToError {

        @Test
        void shouldConvertObjectErrorWithNullTarget() {
            ObjectError objectError = new ObjectError("testBean", "Global error");

            Error error = handler.objectErrorToError(objectError);

            assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(error.target()).isNull();
            assertThat(error.message()).isEqualTo("Global error");
        }

        @Test
        void shouldConvertFieldErrorWithTarget() {
            FieldError fieldError = new FieldError("testBean", "email", "Invalid email format");

            Error error = handler.objectErrorToError(fieldError);

            assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
            assertThat(error.target()).isEqualTo("email");
            assertThat(error.message()).isEqualTo("Invalid email format");
        }
    }

    // =====================================================================
    // addParameterErrors
    // =====================================================================

    @Nested
    class Boot3FluxAddParameterErrors {

        @Test
        void shouldAddCookieTypeErrors() {
            ParameterValidationResult result = buildParameterValidationResult("cookie error");
            List<Error> errorList = new java.util.ArrayList<>();

            handler.addParameterErrors(result, Error.Type.COOKIE, "cookieName", errorList);

            assertThat(errorList).hasSize(1);
            assertThat(errorList.get(0).type()).isEqualTo(Error.Type.COOKIE);
            assertThat(errorList.get(0).target()).isEqualTo("cookieName");
            assertThat(errorList.get(0).message()).isEqualTo("cookie error");
        }

        @Test
        void shouldAddHeaderTypeErrors() {
            ParameterValidationResult result = buildParameterValidationResult("header error");
            List<Error> errorList = new java.util.ArrayList<>();

            handler.addParameterErrors(result, Error.Type.HEADER, "X-Custom-Header", errorList);

            assertThat(errorList).hasSize(1);
            assertThat(errorList.get(0).type()).isEqualTo(Error.Type.HEADER);
            assertThat(errorList.get(0).target()).isEqualTo("X-Custom-Header");
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
    // Visitor branches (via resolveHandlerMethodValidationException)
    // =====================================================================

    @Nested
    class Boot3FluxResolveHandlerMethodValidationExceptionVisitors {

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
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
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
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
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
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
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
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
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
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
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
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
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
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
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
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
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
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
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
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
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
    class Boot3FluxDelegateToSuperHandlers {

        @Test
        void shouldLogHandleMethodNotAllowedException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MethodNotAllowedException ex = new MethodNotAllowedException(HttpMethod.GET, Set.of(HttpMethod.POST));

            h.handleMethodNotAllowedException(ex, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleMethodNotAllowedException"), isNull());
        }

        @Test
        void shouldLogHandleNotAcceptableStatusException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            NotAcceptableStatusException ex = new NotAcceptableStatusException(List.of(MediaType.APPLICATION_JSON));

            h.handleNotAcceptableStatusException(ex, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleNotAcceptableStatusException"), isNull());
        }

        @Test
        void shouldLogHandleUnsupportedMediaTypeStatusException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            UnsupportedMediaTypeStatusException ex = new UnsupportedMediaTypeStatusException(
                    MediaType.TEXT_PLAIN, List.of(MediaType.APPLICATION_JSON));

            h.handleUnsupportedMediaTypeStatusException(ex, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleUnsupportedMediaTypeStatusException"), isNull());
        }

        @Test
        void shouldLogHandleMissingRequestValueException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MissingRequestValueException ex = new MissingRequestValueException(
                    "param", String.class, "query parameter", createMethodParameter());

            h.handleMissingRequestValueException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleMissingRequestValueException"), isNull());
        }

        @Test
        void shouldLogHandleUnsatisfiedRequestParameterException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("key", "value");
            UnsatisfiedRequestParameterException ex = new UnsatisfiedRequestParameterException(
                    List.of("param1=value1"), params);

            h.handleUnsatisfiedRequestParameterException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleUnsatisfiedRequestParameterException"), isNull());
        }

        @Test
        void shouldLogHandleServerWebInputException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ServerWebInputException ex = new ServerWebInputException("Invalid input");

            h.handleServerWebInputException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleServerWebInputException"), isNull());
        }

        @Test
        void shouldLogHandleServerErrorException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ServerErrorException ex = new ServerErrorException("Internal error", (Throwable) null);

            h.handleServerErrorException(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleServerErrorException"), isNull());
        }

        @Test
        void shouldLogHandleResponseStatusException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

            h.handleResponseStatusException(ex, new HttpHeaders(), HttpStatus.NOT_FOUND, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleResponseStatusException"), isNull());
        }

        @Test
        void shouldLogHandleErrorResponseException() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, false);
            org.springframework.web.ErrorResponseException ex =
                    new org.springframework.web.ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);

            h.handleErrorResponseException(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleErrorResponseException"), isNull());
        }
    }

    // =====================================================================
    // Log level and printStackTrace behavior
    // =====================================================================

    @Nested
    class Boot3FluxLogBehavior {

        @Test
        void shouldLogWithExceptionWhenPrintStackTraceEnabled() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, true);
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field", "error"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            h.handleWebExchangeBindException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange)
                    .block();

            verify(mockLogger).debug(eq("handleWebExchangeBindException"), eq(ex));
        }

        @Test
        void shouldNotLogWhenLevelIsOff() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.OFF, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field", "error"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            h.handleWebExchangeBindException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange)
                    .block();

            verifyNoInteractions(mockLogger);
        }

        @Test
        void shouldLogAtInfoLevel() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.INFO, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field", "error"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            h.handleWebExchangeBindException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange)
                    .block();

            verify(mockLogger).info(eq("handleWebExchangeBindException"), isNull());
        }

        @Test
        void shouldLogAtWarnLevel() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.WARN, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field", "error"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            h.handleWebExchangeBindException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange)
                    .block();

            verify(mockLogger).warn(eq("handleWebExchangeBindException"), isNull());
        }

        @Test
        void shouldLogAtErrorLevel() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.ERROR, false);
            BindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
            bindingResult.addError(new FieldError("testBean", "field", "error"));
            WebExchangeBindException ex = new WebExchangeBindException(createMethodParameter(), bindingResult);

            h.handleWebExchangeBindException(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange)
                    .block();

            verify(mockLogger).error(eq("handleWebExchangeBindException"), isNull());
        }

        @Test
        void shouldLogCorrelatedMessageWithoutStackTraceEvenIfPrintStackTraceEnabled() {
            Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger h = handlerWithMockLogger(LogLevel.DEBUG, true);
            // logCorrelated always logs a single-arg message (no Throwable), regardless of printStackTrace setting.
            HandlerMethodValidationException ex = buildExceptionVisiting(visitor ->
                    visitor.cookieValue(annotation(CookieValue.class), buildParameterValidationResult("error")));

            h.resolveHandlerMethodValidationException(ex);

            // logCorrelated uses single-arg debug(String) — no Throwable, even with printStackTrace=true
            verify(mockLogger).debug(matches("\\[exception#[0-9a-f]+] resolveCookieValue"), isNull());
        }
    }

    // =====================================================================
    // Helper methods
    // =====================================================================

    private Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger handlerWithMockLogger(
            LogLevel level, boolean printStackTrace) {
        return new Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger(
                new Boot3CommonExtendedProblemDetailLog(level, printStackTrace), mockLogger);
    }

    private ServerWebExchange mockServerWebExchange() {
        return MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").header(HttpHeaders.ACCEPT_LANGUAGE, "en"));
    }

    private MethodParameter createMethodParameter() {
        try {
            Method method = getClass().getMethod("testMethod", Boot3FluxTestBean.class);
            return new MethodParameter(method, 0);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void testMethod(Boot3FluxTestBean bean) {
        // used for reflection only
    }

    /**
     * Build a ParameterValidationResult mock with a single resolvable error.
     */
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

    /**
     * Build a ParameterErrors mock that returns the given ObjectErrors.
     */
    private ParameterErrors buildParameterErrors(List<ObjectError> objectErrors) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Boot3FluxTestBean(), "testBean");
        objectErrors.forEach(bindingResult::addError);
        return new ParameterErrors(createMethodParameter(), bindingResult.getTarget(), bindingResult, null, null, null);
    }

    /**
     * Build a real MethodValidationException that delegates getParameterValidationResults()
     * and getCrossParameterValidationResults() to the provided lists.
     */
    private org.springframework.validation.method.MethodValidationException buildMethodValidationException(
            List<ParameterValidationResult> results,
            List<MessageSourceResolvable> crossResults) {
        MethodValidationResult mvr = MethodValidationResult.create(
                new Boot3FluxTestBean(),
                getTestMethod(),
                results,
                crossResults);
        return new org.springframework.validation.method.MethodValidationException(mvr);
    }

    /**
     * Build a HandlerMethodValidationException whose visitResults() invokes the given consumer.
     * MethodValidationResult.create() requires at least one ParameterValidationResult,
     * so we supply a placeholder that is never used in the actual visit.
     */
    private HandlerMethodValidationException buildExceptionVisiting(
            java.util.function.Consumer<HandlerMethodValidationException.Visitor> visitorConsumer) {
        ParameterValidationResult placeholder = buildParameterValidationResult("placeholder");
        MethodValidationResult mvr = MethodValidationResult.create(
                new Boot3FluxTestBean(), getTestMethod(), List.of(placeholder));
        return new HandlerMethodValidationException(mvr) {
            @Override
            public void visitResults(@NonNull Visitor visitor) {
                visitorConsumer.accept(visitor);
            }
        };
    }

    /**
     * Build a HandlerMethodValidationException that visits requestParam.
     */
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

    private static Boot3FluxLogVerifier verify(Boot3FluxRecordingLog log) {
        return new Boot3FluxLogVerifier(log);
    }

    private static Boot3FluxMessageExpectation eq(String expected) {
        return actual -> actual.equals(expected);
    }

    private static Boot3FluxThrowableExpectation eq(Throwable expected) {
        return actual -> actual == expected;
    }

    private static Boot3FluxMessageExpectation matches(String regex) {
        return actual -> actual.matches(regex);
    }

    private static Boot3FluxThrowableExpectation isNull() {
        return actual -> actual == null;
    }

    private static void verifyNoInteractions(Boot3FluxRecordingLog log) {
        assertThat(log.events()).isEmpty();
    }

    private Method getTestMethod() {
        try {
            return getClass().getMethod("testMethod", Boot3FluxTestBean.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // =====================================================================
    // Inner classes
    // =====================================================================

    /**
     * Subclass that replaces the protected logger with a mock for log-output verification.
     */
    static class Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger
            extends Boot3FluxExtendedProblemDetailExceptionHandler {

        Boot3FluxExtendedProblemDetailExceptionHandlerWithMockLogger(
                Boot3CommonExtendedProblemDetailLog extendedProblemDetailLog, Log mockLog) {
            super(extendedProblemDetailLog);
            injectLogger(mockLog);
        }

        private void injectLogger(Log mockLog) {
            // The logger field is declared in Boot3FluxExtendedProblemDetailExceptionHandler
            try {
                java.lang.reflect.Field f =
                        Boot3FluxExtendedProblemDetailExceptionHandler.class.getDeclaredField("logger");
                f.setAccessible(true);
                f.set(this, mockLog);
            } catch (Exception e) {
                // Fallback: try ResponseEntityExceptionHandler
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
    interface Boot3FluxMessageExpectation {
        boolean matches(String actual);
    }

    @FunctionalInterface
    interface Boot3FluxThrowableExpectation {
        boolean matches(Throwable actual);
    }

    record Boot3FluxLogEvent(String level, String message, Throwable throwable) {
    }

    static final class Boot3FluxLogVerifier {

        private final Boot3FluxRecordingLog log;

        Boot3FluxLogVerifier(Boot3FluxRecordingLog log) {
            this.log = log;
        }

        void debug(String message, Throwable throwable) {
            assertLogged("debug", actual -> actual.equals(message), actual -> actual == throwable);
        }

        void debug(Boot3FluxMessageExpectation message, Boot3FluxThrowableExpectation throwable) {
            assertLogged("debug", message::matches, throwable::matches);
        }

        void info(String message, Throwable throwable) {
            assertLogged("info", actual -> actual.equals(message), actual -> actual == throwable);
        }

        void info(Boot3FluxMessageExpectation message, Boot3FluxThrowableExpectation throwable) {
            assertLogged("info", message::matches, throwable::matches);
        }

        void warn(String message, Throwable throwable) {
            assertLogged("warn", actual -> actual.equals(message), actual -> actual == throwable);
        }

        void warn(Boot3FluxMessageExpectation message, Boot3FluxThrowableExpectation throwable) {
            assertLogged("warn", message::matches, throwable::matches);
        }

        void error(String message, Throwable throwable) {
            assertLogged("error", actual -> actual.equals(message), actual -> actual == throwable);
        }

        void error(Boot3FluxMessageExpectation message, Boot3FluxThrowableExpectation throwable) {
            assertLogged("error", message::matches, throwable::matches);
        }

        private void assertLogged(String level, Boot3FluxMessageExpectation message, Boot3FluxThrowableExpectation throwable) {
            boolean found = log.events().stream()
                    .anyMatch(event -> event.level().equals(level)
                            && message.matches(event.message())
                            && throwable.matches(event.throwable()));
            assertThat(found).isTrue();
        }
    }

    static final class Boot3FluxRecordingLog implements Log {

        private final List<Boot3FluxLogEvent> events = new ArrayList<>();

        List<Boot3FluxLogEvent> events() {
            return events;
        }

        private void add(String level, Object message, Throwable throwable) {
            events.add(new Boot3FluxLogEvent(level, String.valueOf(message), throwable));
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

    public static class Boot3FluxTestBean {
        private String field1;
        private String field2;
        private String name;
        private String email;
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

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}

package io.github.sbracely.extended.problem.detail.flux.handler;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link FluxExtendedProblemDetailExceptionHandler}.
 *
 * @since 1.0.0
 */
class FluxExtendedProblemDetailExceptionHandlerTests {

    private FluxExtendedProblemDetailExceptionHandler handler;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, false);
        handler = new FluxExtendedProblemDetailExceptionHandler(log);
        exchange = mock(ServerWebExchange.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        when(exchange.getResponse()).thenReturn(response);
    }

    @Test
    void shouldHandleWebExchangeBindException() {
        // Given
        TestBean testBean = new TestBean();
        BindingResult bindingResult = new BeanPropertyBindingResult(testBean, "testBean");
        bindingResult.addError(new FieldError("testBean", "field1", "Field1 is required"));
        bindingResult.addError(new FieldError("testBean", "field2", "Field2 must be positive"));

        WebExchangeBindException ex = new WebExchangeBindException(
                createMethodParameter(), bindingResult);

        // When
        Mono<ResponseEntity<Object>> result = handler.handleWebExchangeBindException(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(responseEntity.getBody()).isInstanceOf(ExtendedProblemDetail.class);

                    ExtendedProblemDetail problemDetail = (ExtendedProblemDetail) responseEntity.getBody();
                    assertThat(problemDetail.getErrors()).hasSize(2);
                    assertThat(problemDetail.getErrors().get(0).target()).isEqualTo("field1");
                    assertThat(problemDetail.getErrors().get(0).message()).isEqualTo("Field1 is required");
                    assertThat(problemDetail.getErrors().get(1).target()).isEqualTo("field2");
                    assertThat(problemDetail.getErrors().get(1).message()).isEqualTo("Field2 must be positive");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleWebExchangeBindExceptionWithObjectError() {
        // Given
        TestBean testBean = new TestBean();
        BindingResult bindingResult = new BeanPropertyBindingResult(testBean, "testBean");
        bindingResult.addError(new ObjectError("testBean", "Object error message"));

        WebExchangeBindException ex = new WebExchangeBindException(
                createMethodParameter(), bindingResult);

        // When
        Mono<ResponseEntity<Object>> result = handler.handleWebExchangeBindException(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    ExtendedProblemDetail problemDetail = (ExtendedProblemDetail) responseEntity.getBody();
                    assertThat(problemDetail.getErrors()).hasSize(1);
                    assertThat(problemDetail.getErrors().get(0).target()).isNull();
                    assertThat(problemDetail.getErrors().get(0).message()).isEqualTo("Object error message");
                })
                .verifyComplete();
    }

    @Test
    void shouldResolveBindingResult() {
        // Given
        TestBean testBean = new TestBean();
        BindingResult bindingResult = new BeanPropertyBindingResult(testBean, "testBean");
        bindingResult.addError(new FieldError("testBean", "name", "Name is required"));

        // When - Using reflection to test protected method
        List<Error> errors = handler.resolveBindingResult(bindingResult);

        // Then
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(0).target()).isEqualTo("name");
        assertThat(errors.get(0).message()).isEqualTo("Name is required");
    }

    @Test
    void shouldConvertObjectErrorToError() {
        // Given
        ObjectError objectError = new ObjectError("testBean", "Global error");

        // When
        Error error = handler.objectErrorToError(objectError);

        // Then
        assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(error.target()).isNull();
        assertThat(error.message()).isEqualTo("Global error");
    }

    @Test
    void shouldConvertFieldErrorToError() {
        // Given
        FieldError fieldError = new FieldError("testBean", "email", "Invalid email format");

        // When
        Error error = handler.objectErrorToError(fieldError);

        // Then
        assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(error.target()).isEqualTo("email");
        assertThat(error.message()).isEqualTo("Invalid email format");
    }

    @Test
    void shouldResolveWebExchangeBindException() {
        // Given
        TestBean testBean = new TestBean();
        BindingResult bindingResult = new BeanPropertyBindingResult(testBean, "testBean");
        bindingResult.addError(new FieldError("testBean", "field", "Field error"));

        WebExchangeBindException ex = new WebExchangeBindException(
                createMethodParameter(), bindingResult);

        // When
        List<Error> errors = handler.resolveWebExchangeBindException(ex);

        // Then
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).target()).isEqualTo("field");
        assertThat(errors.get(0).message()).isEqualTo("Field error");
    }

    // Helper methods and classes

    private MethodParameter createMethodParameter() {
        try {
            Method method = getClass().getMethod("testMethod", TestBean.class);
            return new MethodParameter(method, 0);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void testMethod(TestBean bean) {
        // Test method for reflection
    }

    public static class TestBean {
        private String field1;
        private String field2;
        private String name;
        private String email;
        private String field;

        // Getters and setters
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

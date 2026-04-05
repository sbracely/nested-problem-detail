package io.github.sbracely.extended.problem.detail.mvc.handler;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link MvcExtendedProblemDetailExceptionHandler}.
 *
 * @since 1.0.0
 */
class MvcExtendedProblemDetailExceptionHandlerTests {

    private MvcExtendedProblemDetailExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, false);
        handler = new MvcExtendedProblemDetailExceptionHandler(log);
        webRequest = mock(WebRequest.class);
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        TestBean testBean = new TestBean();
        BindingResult bindingResult = new BeanPropertyBindingResult(testBean, "testBean");
        bindingResult.addError(new FieldError("testBean", "field1", "Field1 is required"));
        bindingResult.addError(new FieldError("testBean", "field2", "Field2 must be positive"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                createMethodParameter(), bindingResult);

        // When
        ResponseEntity<Object> responseEntity = handler.handleMethodArgumentNotValid(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isInstanceOf(ExtendedProblemDetail.class);

        ExtendedProblemDetail problemDetail = (ExtendedProblemDetail) responseEntity.getBody();
        assertThat(problemDetail.getErrors()).hasSize(2);
        assertThat(problemDetail.getErrors().get(0).target()).isEqualTo("field1");
        assertThat(problemDetail.getErrors().get(0).message()).isEqualTo("Field1 is required");
        assertThat(problemDetail.getErrors().get(1).target()).isEqualTo("field2");
        assertThat(problemDetail.getErrors().get(1).message()).isEqualTo("Field2 must be positive");
    }

    @Test
    void shouldHandleWebExchangeBindException() {
        // Given
        TestBean testBean = new TestBean();
        BindingResult bindingResult = new BeanPropertyBindingResult(testBean, "testBean");
        bindingResult.addError(new FieldError("testBean", "name", "Name is required"));

        WebExchangeBindException ex = new WebExchangeBindException(
                createMethodParameter(), bindingResult);

        // When
        List<Error> errors = handler.resolveWebExchangeBindException(ex);

        // Then
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(0).target()).isEqualTo("name");
        assertThat(errors.get(0).message()).isEqualTo("Name is required");
    }

    @Test
    void shouldResolveBindingResult() {
        // Given
        TestBean testBean = new TestBean();
        BindingResult bindingResult = new BeanPropertyBindingResult(testBean, "testBean");
        bindingResult.addError(new FieldError("testBean", "email", "Invalid email format"));
        bindingResult.addError(new ObjectError("testBean", "Global error"));

        // When
        List<Error> errors = handler.resolveBindingResult(bindingResult);

        // Then
        assertThat(errors).hasSize(2);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(0).target()).isEqualTo("email");
        assertThat(errors.get(0).message()).isEqualTo("Invalid email format");
        assertThat(errors.get(1).target()).isNull();
        assertThat(errors.get(1).message()).isEqualTo("Global error");
    }

    @Test
    void shouldConvertObjectErrorToError() {
        // Given
        ObjectError objectError = new ObjectError("testBean", "Global error message");

        // When
        Error error = handler.objectErrorToError(objectError);

        // Then
        assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(error.target()).isNull();
        assertThat(error.message()).isEqualTo("Global error message");
    }

    @Test
    void shouldConvertFieldErrorToError() {
        // Given
        FieldError fieldError = new FieldError("testBean", "username", "Username is too short");

        // When
        Error error = handler.objectErrorToError(fieldError);

        // Then
        assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(error.target()).isEqualTo("username");
        assertThat(error.message()).isEqualTo("Username is too short");
    }

    @Test
    void shouldResolveMethodArgumentNotValidException() {
        // Given
        TestBean testBean = new TestBean();
        BindingResult bindingResult = new BeanPropertyBindingResult(testBean, "testBean");
        bindingResult.addError(new FieldError("testBean", "field", "Field error"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                createMethodParameter(), bindingResult);

        // When
        List<Error> errors = handler.resolveMethodArgumentNotValidException(ex);

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
        private String username;
        private String field;

        // Getters and setters
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

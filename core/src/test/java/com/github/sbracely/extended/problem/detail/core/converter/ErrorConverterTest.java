package com.github.sbracely.extended.problem.detail.core.converter;

import com.github.sbracely.extended.problem.detail.core.response.Error;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ErrorConverter} class.
 */
@ExtendWith(MockitoExtension.class)
class ErrorConverterTest {

    @Test
    void shouldConvertFieldErrorToError() {
        FieldError fieldError = new FieldError("objectName", "email", "must be a valid email");

        Error error = ErrorConverter.objectErrorConvertToError(fieldError);

        assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(error.target()).isEqualTo("email");
        assertThat(error.message()).isEqualTo("must be a valid email");
    }

    @Test
    void shouldConvertObjectErrorToError() {
        ObjectError objectError = new ObjectError("objectName", "object error message");

        Error error = ErrorConverter.objectErrorConvertToError(objectError);

        assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(error.target()).isNull();
        assertThat(error.message()).isEqualTo("object error message");
    }

    @Test
    void shouldConvertObjectErrorWithNullMessage() {
        ObjectError objectError = new ObjectError("objectName", (String) null);

        Error error = ErrorConverter.objectErrorConvertToError(objectError);

        assertThat(error.type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(error.target()).isNull();
        assertThat(error.message()).isNull();
    }

    @Test
    void shouldConvertMethodValidationExceptionWithParameterErrors() {
        // Create mock objects
        ParameterErrors parameterErrors = mock(ParameterErrors.class);
        FieldError fieldError = new FieldError("objectName", "name", "must not be blank");
        when(parameterErrors.getAllErrors()).thenReturn(List.of(fieldError));

        MethodValidationException ex = mock(MethodValidationException.class);
        when(ex.getParameterValidationResults()).thenReturn(List.of(parameterErrors));
        when(ex.getCrossParameterValidationResults()).thenReturn(Collections.emptyList());

        List<Error> errors = ErrorConverter.methodValidationExceptionConvertToError(ex);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(0).target()).isEqualTo("name");
        assertThat(errors.get(0).message()).isEqualTo("must not be blank");
    }

    @Test
    void shouldConvertMethodValidationExceptionWithParameterValidationResult() throws NoSuchMethodException {
        // Create mock objects
        ParameterValidationResult validationResult = mock(ParameterValidationResult.class);
        MessageSourceResolvable resolvableError = mock(MessageSourceResolvable.class);

        Method method = TestService.class.getMethod("testMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        when(validationResult.getMethodParameter()).thenReturn(methodParameter);
        when(validationResult.getResolvableErrors()).thenReturn(List.of(resolvableError));
        when(resolvableError.getDefaultMessage()).thenReturn("parameter validation error");

        MethodValidationException ex = mock(MethodValidationException.class);
        when(ex.getParameterValidationResults()).thenReturn(List.of(validationResult));
        when(ex.getCrossParameterValidationResults()).thenReturn(Collections.emptyList());

        List<Error> errors = ErrorConverter.methodValidationExceptionConvertToError(ex);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        // Parameter name will be null since no ParameterNameDiscoverer is set
        assertThat(errors.get(0).message()).isEqualTo("parameter validation error");
    }

    @Test
    void shouldConvertCrossParameterValidationResults() {
        MessageSourceResolvable crossParamError = mock(MessageSourceResolvable.class);
        when(crossParamError.getDefaultMessage()).thenReturn("cross parameter error");

        MethodValidationException ex = mock(MethodValidationException.class);
        when(ex.getParameterValidationResults()).thenReturn(Collections.emptyList());
        when(ex.getCrossParameterValidationResults()).thenReturn(List.of(crossParamError));

        List<Error> errors = ErrorConverter.methodValidationExceptionConvertToError(ex);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(0).target()).isNull();
        assertThat(errors.get(0).message()).isEqualTo("cross parameter error");
    }

    @Test
    void shouldReturnEmptyListForEmptyValidationResults() {
        MethodValidationException ex = mock(MethodValidationException.class);
        when(ex.getParameterValidationResults()).thenReturn(Collections.emptyList());
        when(ex.getCrossParameterValidationResults()).thenReturn(Collections.emptyList());

        List<Error> errors = ErrorConverter.methodValidationExceptionConvertToError(ex);

        assertThat(errors).isEmpty();
    }

    /**
     * Test service class for method parameter testing.
     */
    @SuppressWarnings("unused")
    static class TestService {
        public void testMethod(String param) {
        }
    }
}

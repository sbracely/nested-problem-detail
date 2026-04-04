package com.github.sbracely.extended.problem.detail.core.handler;

import com.github.sbracely.extended.problem.detail.core.logging.ExtendedProblemDetailLog;
import com.github.sbracely.extended.problem.detail.core.response.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ValidationErrorHandler} class.
 */
@ExtendWith(MockitoExtension.class)
class ValidationErrorHandlerTest {

    @Mock
    private ExtendedProblemDetailLog extendedProblemDetailLog;

    private ValidationErrorHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ValidationErrorHandler(extendedProblemDetailLog);
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        // Create a binding result with errors
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                new Object(), "target");
        bindingResult.addError(new ObjectError("object", "Object error message"));
        bindingResult.addError(new FieldError("object", "fieldName", "Field error message"));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        List<Error> errors = handler.handleMethodArgumentNotValidException(exception);

        assertThat(errors).hasSize(2);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(0).target()).isNull();
        assertThat(errors.get(0).message()).isEqualTo("Object error message");
        assertThat(errors.get(1).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(1).target()).isEqualTo("fieldName");
        assertThat(errors.get(1).message()).isEqualTo("Field error message");
    }

    @Test
    void shouldConvertMethodValidationExceptionToErrors() {
        // Create a mock MethodValidationException
        MethodParameter methodParameter = MethodParameter.forExecutable(
                getMethod("testMethod"), 0);
        methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());

        ParameterValidationResult result = mock(ParameterValidationResult.class);
        when(result.getMethodParameter()).thenReturn(methodParameter);
        when(result.getResolvableErrors()).thenReturn(List.of(
                new DefaultMessageSourceResolvable(new String[]{"error.code"}, "Error message")
        ));

        MethodValidationException exception = mock(MethodValidationException.class);
        when(exception.getParameterValidationResults()).thenReturn(List.of(result));
        when(exception.getCrossParameterValidationResults()).thenReturn(List.of());

        List<Error> errors = handler.handleMethodValidationException(exception);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(0).message()).isEqualTo("Error message");
    }

    @Test
    void shouldConvertParameterErrorsToErrors() {
        // Create ParameterErrors (when validation is on a complex object)
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                new Object(), "target");
        bindingResult.addError(new FieldError("target", "field1", "Field 1 error"));
        bindingResult.addError(new FieldError("target", "field2", "Field 2 error"));

        MethodParameter methodParameter = MethodParameter.forExecutable(
                getMethod("testMethod"), 0);

        ParameterErrors parameterErrors = mock(ParameterErrors.class);
        when(parameterErrors.getAllErrors()).thenReturn(bindingResult.getAllErrors());

        MethodValidationException exception = mock(MethodValidationException.class);
        when(exception.getParameterValidationResults()).thenReturn(List.of(parameterErrors));
        when(exception.getCrossParameterValidationResults()).thenReturn(List.of());

        List<Error> errors = handler.handleMethodValidationException(exception);

        assertThat(errors).hasSize(2);
        assertThat(errors.get(0).target()).isEqualTo("field1");
        assertThat(errors.get(1).target()).isEqualTo("field2");
    }

    @Test
    void shouldHandleCrossParameterValidationErrors() {
        MethodValidationException exception = mock(MethodValidationException.class);
        when(exception.getParameterValidationResults()).thenReturn(List.of());

        // Cross parameter results are MessageSourceResolvable, not ParameterValidationResult
        org.springframework.context.MessageSourceResolvable crossParamResult = 
            mock(org.springframework.context.MessageSourceResolvable.class);
        when(crossParamResult.getDefaultMessage()).thenReturn("Cross parameter error");

        when(exception.getCrossParameterValidationResults()).thenReturn(List.of(crossParamResult));

        List<Error> errors = handler.handleMethodValidationException(exception);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(0).target()).isNull();
        assertThat(errors.get(0).message()).isEqualTo("Cross parameter error");
    }

    @Test
    void shouldProcessHandlerMethodValidationException() {
        // This test verifies the visitor pattern works correctly
        // We'll create a mock HandlerMethodValidationException
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);

        // Capture the visitor when visitResults is called
        doAnswer(invocation -> {
            HandlerMethodValidationException.Visitor visitor = invocation.getArgument(0);
            // Simulate visiting a request param
            ParameterValidationResult result = createParameterValidationResult("param");
            visitor.requestParam(null, result);
            return null;
        }).when(exception).visitResults(any(HandlerMethodValidationException.Visitor.class));

        List<Error> errors = handler.handleHandlerMethodValidationException(exception);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(0).target()).isEqualTo("param");
    }

    @Test
    void shouldHandleCookieValueParameter() {
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);

        doAnswer(invocation -> {
            HandlerMethodValidationException.Visitor visitor = invocation.getArgument(0);
            CookieValue cookieValue = mock(CookieValue.class);
            ParameterValidationResult result = createParameterValidationResult("sessionId");
            visitor.cookieValue(cookieValue, result);
            return null;
        }).when(exception).visitResults(any(HandlerMethodValidationException.Visitor.class));

        List<Error> errors = handler.handleHandlerMethodValidationException(exception);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.COOKIE);
    }

    @Test
    void shouldHandleRequestHeaderParameter() {
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);

        doAnswer(invocation -> {
            HandlerMethodValidationException.Visitor visitor = invocation.getArgument(0);
            RequestHeader requestHeader = mock(RequestHeader.class);
            ParameterValidationResult result = createParameterValidationResult("X-Auth-Token");
            visitor.requestHeader(requestHeader, result);
            return null;
        }).when(exception).visitResults(any(HandlerMethodValidationException.Visitor.class));

        List<Error> errors = handler.handleHandlerMethodValidationException(exception);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.HEADER);
    }

    @Test
    void shouldHandlePathVariableParameter() {
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);

        doAnswer(invocation -> {
            HandlerMethodValidationException.Visitor visitor = invocation.getArgument(0);
            PathVariable pathVariable = mock(PathVariable.class);
            ParameterValidationResult result = createParameterValidationResult("param");
            visitor.pathVariable(pathVariable, result);
            return null;
        }).when(exception).visitResults(any(HandlerMethodValidationException.Visitor.class));

        List<Error> errors = handler.handleHandlerMethodValidationException(exception);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).type()).isEqualTo(Error.Type.PARAMETER);
        assertThat(errors.get(0).target()).isEqualTo("param");
    }

    @Test
    void shouldHandleOtherParameterAndLog() {
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);

        doAnswer(invocation -> {
            HandlerMethodValidationException.Visitor visitor = invocation.getArgument(0);
            ParameterValidationResult result = createParameterValidationResultWithResolvableErrors();
            visitor.other(result);
            return null;
        }).when(exception).visitResults(any(HandlerMethodValidationException.Visitor.class));

        List<Error> errors = handler.handleHandlerMethodValidationException(exception);

        // handleOther doesn't add errors to the list, only logs
        assertThat(errors).isEmpty();
        // Verify logging was called
        verify(extendedProblemDetailLog, atLeastOnce()).log(any(), any(), anyString(), any(), any());
    }

    @Test
    void shouldHandleRequestBodyWithParameterErrors() {
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);

        doAnswer(invocation -> {
            HandlerMethodValidationException.Visitor visitor = invocation.getArgument(0);
            org.springframework.web.bind.annotation.RequestBody requestBody =
                mock(org.springframework.web.bind.annotation.RequestBody.class);

            // Create ParameterErrors mock
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                    new Object(), "target");
            bindingResult.addError(new FieldError("target", "email", "Invalid email"));

            ParameterErrors errors = mock(ParameterErrors.class);
            when(errors.getAllErrors()).thenReturn(bindingResult.getAllErrors());

            visitor.requestBody(requestBody, errors);
            return null;
        }).when(exception).visitResults(any(HandlerMethodValidationException.Visitor.class));

        List<Error> result = handler.handleHandlerMethodValidationException(exception);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).target()).isEqualTo("email");
        assertThat(result.get(0).message()).isEqualTo("Invalid email");
    }

    @Test
    void shouldHandleMultipleValidationErrors() {
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);

        doAnswer(invocation -> {
            HandlerMethodValidationException.Visitor visitor = invocation.getArgument(0);

            // Simulate multiple parameter validations
            ParameterValidationResult result1 = createParameterValidationResult("param1");
            ParameterValidationResult result2 = createParameterValidationResult("param2");

            visitor.requestParam(mock(RequestParam.class), result1);
            visitor.requestParam(mock(RequestParam.class), result2);
            return null;
        }).when(exception).visitResults(any(HandlerMethodValidationException.Visitor.class));

        List<Error> errors = handler.handleHandlerMethodValidationException(exception);

        assertThat(errors).hasSize(2);
    }

    // Helper methods

    private Method getMethod(String name) {
        try {
            return TestController.class.getMethod(name, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private ParameterValidationResult createParameterValidationResult(String parameterName) {
        ParameterValidationResult result = mock(ParameterValidationResult.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(
                getMethod("testMethod"), 0);
        methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        
        when(result.getMethodParameter()).thenReturn(methodParameter);
        when(result.getResolvableErrors()).thenReturn(List.of(
                new DefaultMessageSourceResolvable(new String[]{"error"}, "Error message")
        ));
        return result;
    }

    private ParameterValidationResult createParameterValidationResultWithResolvableErrors() {
        ParameterValidationResult result = mock(ParameterValidationResult.class);
        when(result.getResolvableErrors()).thenReturn(List.of(
                new DefaultMessageSourceResolvable(new String[]{"code1", "code2"}, "Default message")
        ));
        return result;
    }

    @SuppressWarnings("unused")
    static class TestController {
        public void testMethod(String param) {
        }
    }
}

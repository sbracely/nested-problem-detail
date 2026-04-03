package com.github.sbracely.extended.problem.detail.test.mvc.test;

import com.github.sbracely.extended.problem.detail.core.response.Error;
import com.github.sbracely.extended.problem.detail.core.response.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.test.mvc.config.MethodValidationConfiguration;
import com.github.sbracely.extended.problem.detail.test.mvc.controller.MvcProblemDetailController;
import com.github.sbracely.extended.problem.detail.test.mvc.endpoint.DemoEndpoint;
import com.github.sbracely.extended.problem.detail.test.mvc.exception.ExtendedErrorResponseException;
import com.github.sbracely.extended.problem.detail.test.mvc.request.ProblemDetailRequest;
import com.github.sbracely.extended.problem.detail.test.mvc.service.ProblemDetailService;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.actuate.endpoint.web.AbstractWebMvcEndpointHandlerMapping;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.*;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class MvcExtendedProblemDetailTests {

    @Autowired
    private MockMvcTester mockMvcTester;

    private static final String BASE_PATH = "/mvc-extended-problem-detail";

    /**
     * @see HttpRequestMethodNotSupportedException
     * @see MvcProblemDetailController#httpRequestMethodNotSupportedException()
     */
    @Test
    void httpRequestMethodNotSupportedException() {
        String uri = BASE_PATH + "/http-request-method-not-supported-exception";
        MvcTestResult result = mockMvcTester.post().uri(uri).exchange();
        assertThat(result)
                .hasStatus(METHOD_NOT_ALLOWED)
                .hasContentType(APPLICATION_PROBLEM_JSON)
                .hasHeader(ALLOW, GET.name());
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Method 'POST' is not supported.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see HttpMediaTypeNotSupportedException
     * @see MvcProblemDetailController#httpMediaTypeNotSupportedException(ProblemDetailRequest)
     */
    @Test
    void httpMediaTypeNotSupportedException() {
        String uri = BASE_PATH + "/http-media-type-not-supported-exception";
        MvcTestResult result = mockMvcTester.put().uri(uri).exchange();
        assertThat(result)
                .hasStatus(UNSUPPORTED_MEDIA_TYPE)
                .hasContentType(APPLICATION_PROBLEM_JSON)
                .hasHeader(ACCEPT, APPLICATION_JSON_VALUE);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Content-Type 'null' is not supported.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see HttpMediaTypeNotAcceptableException
     * @see MvcProblemDetailController#httpMediaTypeNotAcceptableException(ProblemDetailRequest)
     */
    @Test
    void httpMediaTypeNotAcceptableException() {
        String uri = BASE_PATH + "/http-media-type-not-acceptable-exception";
        MvcTestResult result = mockMvcTester.put().uri(uri)
                .header(ACCEPT, APPLICATION_XML_VALUE).exchange();
        assertThat(result)
                .hasStatus(NOT_ACCEPTABLE)
                .hasContentType(APPLICATION_PROBLEM_JSON)
                .hasHeader(ACCEPT, APPLICATION_JSON_VALUE);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_ACCEPTABLE.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Acceptable representations: [application/json].");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MissingPathVariableException
     * @see MvcProblemDetailController#missingPathVariableException(Integer)
     */
    @Test
    void missingPathVariableException() {
        String uri = BASE_PATH + "/missing-path-variable-exception";
        MvcTestResult result = mockMvcTester.delete().uri(uri).exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).contains("Required path variable");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MissingServletRequestParameterException
     * @see MvcProblemDetailController#missingServletRequestParameterException(Integer)
     */
    @Test
    void missingServletRequestParameterException() {
        String uri = BASE_PATH + "/missing-servlet-request-parameter-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required parameter 'id' is not present.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MissingServletRequestPartException
     * @see MvcProblemDetailController#missingServletRequestPartException(MultipartFile)
     */
    @Test
    void missingServletRequestPartException() {
        String uri = BASE_PATH + "/missing-servlet-request-part-exception";
        MvcTestResult result = mockMvcTester.put().multipart().contentType(MULTIPART_FORM_DATA).uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required part 'file' is not present.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see ServletRequestBindingException
     * @see MvcProblemDetailController#servletRequestBindingException()
     */
    @Test
    void servletRequestBindingException() {
        String uri = BASE_PATH + "/servlet-request-binding-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see UnsatisfiedServletRequestParameterException
     * @see MvcProblemDetailController#unsatisfiedServletRequestParameterException()
     */
    @Test
    void unsatisfiedServletRequestParameterException() {
        String uri = BASE_PATH + "/unsatisfied-servlet-request-parameter-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).param("type", "1").exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid request parameters.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    /**
     * @see org.springframework.web.bind.MissingRequestValueException
     * @see MvcProblemDetailController#orgSpringWebBindMissingRequestValueException()
     */
    @Test
    void orgSpringWebBindMissingRequestValueException() {
        String uri = BASE_PATH + "/org-spring-web-bind-missing-request-value-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MissingMatrixVariableException
     * @see MvcProblemDetailController#missingMatrixVariableException(String, List)
     */
    @Test
    void missingMatrixVariableException() {
        String uri = BASE_PATH + "/missing-matrix-variable-exception/abc;list1=a,b,c";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).contains("Required path parameter 'list' is not present.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MissingRequestCookieException
     * @see MvcProblemDetailController#missingRequestCookieException(String)
     */
    @Test
    void missingRequestCookieException() {
        String uri = BASE_PATH + "/missing-request-cookie-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required cookie 'cookieValue' is not present.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MissingRequestHeaderException
     * @see MvcProblemDetailController#missingRequestHeaderException(String)
     */
    @Test
    void missingRequestHeaderException() {
        String uri = BASE_PATH + "/missing-request-header-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required header 'header' is not present.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MethodArgumentNotValidException
     * @see MvcProblemDetailController#methodArgumentNotValidException(ProblemDetailRequest)
     */
    @Test
    void methodArgumentNotValidException() {
        String uri = BASE_PATH + "/method-argument-not-valid-exception";
        MvcTestResult result = mockMvcTester.post().uri(uri).contentType(APPLICATION_JSON).content("""
                                {
                                    "name": "abc",
                                    "password": "123"
                                }
                """).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid request content.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.PARAMETER, "name", "Name length must be between 6-10"),
                new Error(Error.Type.PARAMETER, "age", "Age cannot be null"),
                new Error(Error.Type.PARAMETER, "password", "Password and confirm password do not match"),
                new Error(Error.Type.PARAMETER, "confirmPassword", "Password and confirm password do not match")
        );
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionCookieValue(String)
     * @see HandlerMethodValidationException.Visitor#cookieValue(CookieValue, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionCookieValue() {
        String uri = BASE_PATH + "/handler-method-validation-exception-cookie-value";
        MvcTestResult result = mockMvcTester.get().uri(uri).cookie(new Cookie("name", "a")).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.COOKIE, "name", "Name length must be at least 2"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionMatrixVariable(String, List)
     * @see HandlerMethodValidationException.Visitor#matrixVariable(MatrixVariable, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionMatrixVariable() {
        String uri = BASE_PATH + "/handler-method-validation-exception-matrix-variable/abc;list=a,b,c";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "list", "Maximum size is 2"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionModelAttribute(ProblemDetailRequest)
     * @see HandlerMethodValidationException.Visitor#modelAttribute(ModelAttribute, ParameterErrors)
     */
    @Test
    void handlerMethodValidationExceptionModelAttribute() {
        String uri = BASE_PATH + "/handler-method-validation-exception-model-attribute";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "password", "Password cannot be empty"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionPathVariable(String)
     * @see HandlerMethodValidationException.Visitor#pathVariable(PathVariable, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionPathVariable() {
        String uri = BASE_PATH + "/handler-method-validation-exception-path-variable/a";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "id", "ID minimum length is 2"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionRequestBody(ProblemDetailRequest)
     * @see HandlerMethodValidationException.Visitor#requestBody(RequestBody, ParameterErrors)
     */
    @Test
    void handlerMethodValidationExceptionRequestBody() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-body";
        MvcTestResult result = mockMvcTester.post().uri(uri).content("""
                {
                    "name": "abc"
                }
                """).contentType(APPLICATION_JSON).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "password", "Password cannot be empty"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionRequestBodyValidationResult(List)
     * @see HandlerMethodValidationException.Visitor#requestBodyValidationResult(RequestBody, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionRequestBodyValidationResult() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-body-validation-result";
        MvcTestResult result = mockMvcTester.post().uri(uri).contentType(APPLICATION_JSON).content("""
                              ["","a"]
                """).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, null, "Element cannot contain empty values"));

    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionRequestHeader(String)
     * @see HandlerMethodValidationException.Visitor#requestHeader(RequestHeader, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionRequestHeader() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-header";
        MvcTestResult result = mockMvcTester.get().uri(uri).header("headerValue", "a").exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.HEADER, "headerValue", "Minimum length is 2"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionRequestParam(String, String)
     * @see HandlerMethodValidationException.Visitor#requestParam(RequestParam, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionRequestParam() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-param";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.PARAMETER, "param", "Parameter cannot be empty"),
                new Error(Error.Type.PARAMETER, "param2", "Parameter 2 cannot be null"),
                new Error(Error.Type.PARAMETER, "param2", "Parameter 2 cannot be blank")
        );
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionRequestPart(MultipartFile)
     * @see HandlerMethodValidationException.Visitor#requestPart(RequestPart, ParameterErrors)
     */
    @Test
    void handlerMethodValidationExceptionRequestPart() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-part";
        MvcTestResult result = mockMvcTester.get().uri(uri)
                .header(CONTENT_TYPE, MULTIPART_FORM_DATA).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "file", "File cannot be empty"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionOther(String, String, String)
     * @see HandlerMethodValidationException.Visitor#other(ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionOther() {
        String uri = BASE_PATH + "/handler-method-validation-exception-other";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see NoHandlerFoundException
     */
    @Nested
    @TestPropertySource(properties = "spring.web.resources.add-mappings=false")
    class NoHandlerFoundExceptionTest {
        @Test
        void noHandlerFoundException() {
            String uri = BASE_PATH + "/no-handler-found-exception";
            MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
            assertThat(result)
                    .hasStatus(NOT_FOUND)
                    .hasContentType(APPLICATION_PROBLEM_JSON);
            ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                    .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
            log.info("extendedProblemDetail: {}", extendedProblemDetail);
            assertThat(extendedProblemDetail.getType()).isNull();
            assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
            assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_FOUND.value());
            assertThat(extendedProblemDetail.getDetail()).isEqualTo("No endpoint %s %s.".formatted(GET.name(), uri));
            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(extendedProblemDetail.getProperties()).isNull();
            assertThat(extendedProblemDetail.getErrors()).isNull();
        }
    }

    /**
     * @see NoResourceFoundException
     */
    @Test
    void noResourceFoundException() {
        String uri = BASE_PATH + "/no-resource-found-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(NOT_FOUND)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_FOUND.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("No static resource %s.".formatted(uri.replaceFirst("/", "")));
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see AsyncRequestTimeoutException
     * @see MvcProblemDetailController#asyncRequestTimeoutException()
     */
    @Test
    void asyncRequestTimeoutException() throws IOException {
        String uri = BASE_PATH + "/async-request-timeout-exception";
        MvcTestResult mvcTestResult = mockMvcTester.get().uri(uri).asyncExchange();
        assertThat(mvcTestResult.getRequest().isAsyncStarted()).isTrue();
        AsyncContext asyncContext = mvcTestResult.getRequest().getAsyncContext();
        assertThat(asyncContext).isNotNull();
        AsyncListener listener = ((MockAsyncContext) asyncContext).getListeners().get(0);
        listener.onTimeout(null);
        MvcTestResult result = mockMvcTester.perform(MockMvcRequestBuilders.asyncDispatch(mvcTestResult.getMvcResult()));
        assertThat(result)
                .hasStatus(SERVICE_UNAVAILABLE)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(SERVICE_UNAVAILABLE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(SERVICE_UNAVAILABLE.value());
        assertThat(extendedProblemDetail.getDetail()).isNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see ErrorResponseException
     * @see MvcProblemDetailController#errorResponseException()
     */
    @Test
    void errorResponseException() {
        String uri = BASE_PATH + "/error-response-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getDetail()).isNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getErrors()).isNull();
        assertThat(extendedProblemDetail.getProperties()).isNull();
    }

    /**
     * @see ExtendedErrorResponseException
     * @see MvcProblemDetailController#extendedErrorResponseException()
     */
    @Test
    void extendedErrorResponseException() {
        String uri = BASE_PATH + "/extended-error-response-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Payment failed");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.BUSINESS, null, "Insufficient balance"),
                new Error(Error.Type.BUSINESS, null, "Payment frequent")
        );
    }

    /**
     * @see ResponseStatusException
     * @see MvcProblemDetailController#responseStatusException()
     */
    @Test
    void responseStatusException() {
        String uri = BASE_PATH + "/response-status-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("exception");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see AbstractWebMvcEndpointHandlerMapping.InvalidEndpointBadRequestException
     * @see DemoEndpoint#hello(String, String, String)
     */
    @Nested
    @TestPropertySource(properties = "management.endpoints.web.exposure.include=demo")
    class InvalidEndpointBadRequestExceptionTests {
        private static final String BASE_PATH = "/actuator";

        @Test
        void invalidEndpointBadRequestException() {
            String uri = BASE_PATH + "/demo/name";
            MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
            assertThat(result)
                    .hasStatus(BAD_REQUEST)
                    .hasContentType(APPLICATION_PROBLEM_JSON);
            ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                    .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
            log.info("extendedProblemDetail: {}", extendedProblemDetail);
            assertThat(extendedProblemDetail.getDetail()).containsOnlyOnce("Missing parameters: ")
                    .contains("param1", "param2");
            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(extendedProblemDetail.getErrors()).isNull();
        }
    }

    /**
     * @see ServerWebInputException
     * @see MvcProblemDetailController#serverWebInputException()
     */
    @Test
    void serverWebInputException() {
        String uri = BASE_PATH + "/server-web-input-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("server web input error");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see UnsatisfiedRequestParameterException
     * @see MvcProblemDetailController#unsatisfiedRequestParameterException()
     */
    @Test
    void unsatisfiedRequestParameterException() {
        String uri = BASE_PATH + "/unsatisfied-request-parameter-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).param("type", "1").exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isNotNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see org.springframework.web.server.MissingRequestValueException
     * @see MvcProblemDetailController#orgSpringframeworkWebServerMissingRequestValueException(HttpServletRequest, String)
     */
    @Test
    void orgSpringframeworkWebServerMissingRequestValueException() {
        String uri = BASE_PATH + "/org-springframework-web-server-missing-request-value-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required request param 'id' is not present.");
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    /**
     * @see WebExchangeBindException
     * @see MvcProblemDetailController#webExchangeBindException(HttpServletRequest, ProblemDetailRequest, BindingResult)
     */
    @Test
    void webExchangeBindException() {
        String uri = BASE_PATH + "/web-exchange-bind-exception";
        MvcTestResult result = mockMvcTester.post().uri(uri).contentType(APPLICATION_JSON).content("""
                                {
                                    "name": "abc",
                                    "password": "123"
                                }
                """).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid request content.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.PARAMETER, "name", "Name length must be between 6-10"),
                new Error(Error.Type.PARAMETER, "age", "Age cannot be null"),
                new Error(Error.Type.PARAMETER, "password", "Password and confirm password do not match"),
                new Error(Error.Type.PARAMETER, "confirmPassword", "Password and confirm password do not match")
        );
    }

    /**
     * @see MethodNotAllowedException
     * @see MvcProblemDetailController#methodNotAllowedException(HttpMethod)
     */
    @Test
    void methodNotAllowedException() {
        String uri = BASE_PATH + "/method-not-allowed-exception";
        MvcTestResult result = mockMvcTester.delete().uri(uri).exchange();
        assertThat(result)
                .hasStatus(METHOD_NOT_ALLOWED)
                .hasContentType(APPLICATION_PROBLEM_JSON)
                .containsHeader(ALLOW)
                .headers()
                .hasHeaderSatisfying(ALLOW, header ->
                        assertThat(header)
                                .singleElement()
                                .matches(h -> h.contains(GET.name()) && h.contains(POST.name())));
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
        assertThat(extendedProblemDetail.getDetail()).startsWith("Supported methods: [")
                .contains("GET", "POST").endsWith("]");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see NotAcceptableStatusException
     * @see MvcProblemDetailController#notAcceptableStatusException()
     */
    @Test
    void notAcceptableStatusException() {
        String uri = BASE_PATH + "/not-acceptable-status-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(NOT_ACCEPTABLE)
                .hasContentType(APPLICATION_PROBLEM_JSON)
                .hasHeader(ACCEPT, APPLICATION_JSON_VALUE);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_ACCEPTABLE.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Acceptable representations: [application/json].");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see ContentTooLargeException
     * @see MvcProblemDetailController#contentTooLargeException(MultipartFile)
     */
    @Test
    void contentTooLargeException() {
        String uri = BASE_PATH + "/content-too-large-exception";
        MockMultipartFile file = new MockMultipartFile("file", "test-upload.txt",
                "text/plain", "Hello, this is a test file content!".getBytes(StandardCharsets.UTF_8));
        MvcTestResult result = mockMvcTester.perform(multipart(uri).file(file));
        assertThat(result)
                .hasStatus(CONTENT_TOO_LARGE)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
        assertThat(extendedProblemDetail.getDetail()).isNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see UnsupportedMediaTypeStatusException
     * @see MvcProblemDetailController#unsupportedMediaTypeStatusException()
     */
    @Test
    void unsupportedMediaTypeStatusException() {
        String uri = BASE_PATH + "/unsupported-media-type-status-exception";
        MvcTestResult result = mockMvcTester.post().uri(uri).exchange();
        assertThat(result)
                .hasStatus(UNSUPPORTED_MEDIA_TYPE)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Could not parse Content-Type.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see ServerErrorException
     * @see MvcProblemDetailController#serverErrorException()
     */
    @Test
    void serverErrorException() {
        String uri = BASE_PATH + "/server-error-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("server error");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see PayloadTooLargeException
     * @see MvcProblemDetailController#payloadTooLargeException(MultipartFile)
     */
    @Test
    void payloadTooLargeException() {
        String uri = BASE_PATH + "/payload-too-large-exception";
        MockMultipartFile file = new MockMultipartFile("file", "test-upload.txt",
                "text/plain", "test content".getBytes(StandardCharsets.UTF_8));
        MvcTestResult result = mockMvcTester.perform(multipart(uri).file(file));
        assertThat(result)
                .hasStatus(CONTENT_TOO_LARGE)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("payload too large");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see ConversionNotSupportedException
     * @see MvcProblemDetailController#conversionNotSupportedException(HttpServletRequest, String)
     */
    @Test
    void conversionNotSupportedException() {
        String uri = BASE_PATH + "/conversion-not-supported-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).param("data", "test-value").exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).contains("Failed to convert");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MethodArgumentConversionNotSupportedException
     * @see MvcProblemDetailController#methodArgumentConversionNotSupportedException(ProblemDetailRequest)
     */
    @Test
    void methodArgumentConversionNotSupportedException() {
        String uri = BASE_PATH + "/method-argument-conversion-not-supported-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).param("error", "test-value").exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).contains("Failed to convert");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see TypeMismatchException
     * @see MvcProblemDetailController#typeMismatchException()
     */
    @Test
    void typeMismatchException() {
        String uri = BASE_PATH + "/type-mismatch-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).param("integer", "a").exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).contains("Failed to convert");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MethodArgumentTypeMismatchException
     * @see MvcProblemDetailController#methodArgumentTypeMismatchException(Integer)
     */
    @Test
    void methodArgumentTypeMismatchException() {
        String uri = BASE_PATH + "/method-argument-type-mismatch-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).param("integer", "a").exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).contains("Failed to convert").contains("'a'");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see HttpMessageNotReadableException
     * @see MvcProblemDetailController#httpMessageNotReadableException(ProblemDetailRequest)
     */
    @Test
    void httpMessageNotReadableException() {
        String uri = BASE_PATH + "/http-message-not-readable-exception";
        MvcTestResult result = mockMvcTester.post().uri(uri).contentType(APPLICATION_JSON).content("""
                           {
                """).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Failed to read request");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see HttpMessageNotWritableException
     * @see MvcProblemDetailController#httpMessageNotWritableException()
     */
    @Test
    void httpMessageNotWritableException() {
        String uri = BASE_PATH + "/http-message-not-writable-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Failed to write request");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MethodValidationException
     * @see MvcProblemDetailController#methodValidationException()
     * @see ProblemDetailService#createProblemDetail(String, ProblemDetailRequest)
     * @see MethodValidationConfiguration#validationPostProcessor()
     */
    @Test
    void methodValidationException() {
        String uri = BASE_PATH + "/method-validation-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failed");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.PARAMETER, "name", "name must not be blank"),
                new Error(Error.Type.PARAMETER, "name", "name must not be null"),
                new Error(Error.Type.PARAMETER, "password", "Password and confirm password do not match"),
                new Error(Error.Type.PARAMETER, "name", "Name cannot be blank"),
                new Error(Error.Type.PARAMETER, "age", "Age cannot be null"),
                new Error(Error.Type.PARAMETER, "confirmPassword", "Password and confirm password do not match"),
                new Error(Error.Type.PARAMETER, "name", "Name length must be between 6-10"),
                new Error(Error.Type.PARAMETER, null, "Name is not valid")
        );
    }
}

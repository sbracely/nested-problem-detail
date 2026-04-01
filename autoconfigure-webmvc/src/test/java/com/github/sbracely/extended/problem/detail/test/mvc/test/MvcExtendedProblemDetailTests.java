package com.github.sbracely.extended.problem.detail.test.mvc.test;

import com.github.sbracely.extended.problem.detail.response.Error;
import com.github.sbracely.extended.problem.detail.response.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.test.mvc.controller.MvcProblemDetailController;
import com.github.sbracely.extended.problem.detail.test.mvc.exception.BusinessException;
import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.ProblemDetailRequest;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.InvalidApiVersionException;
import org.springframework.web.accept.MissingApiVersionException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.apache.tomcat.util.http.Method.GET;
import static org.apache.tomcat.util.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
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
                        assertThat(header).contains(HttpMethod.GET.name(), HttpMethod.POST.name()));
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
        assertThat(extendedProblemDetail.getDetail()).isNull();
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
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required query parameter 'id' is not present.");
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
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required request part 'file' is not present.");
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
        assertThat(extendedProblemDetail.getDetail()).contains("Required matrix variable");
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
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required parameter 'param' is not present.");
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
     * @see InvalidApiVersionException
     * @see MvcProblemDetailController#invalidApiVersionException()
     */
    @Test
    void invalidApiVersionException() {
        String uri = BASE_PATH + "/invalid-api-version-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri)
                .header("API-Version", "1").exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid API version: '1.0.0'.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MissingApiVersionException
     * @see MvcProblemDetailController#missingApiVersionException()
     */
    @Test
    void missingApiVersionException() {
        String uri = BASE_PATH + "/missing-api-version-exception";
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
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("API version is required.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MethodNotAllowedException
     * @see MvcProblemDetailController#methodNotAllowedException(HttpMethod)
     */
    @Test
    void methodNotAllowedExceptionDuplicate() {
        String uri = BASE_PATH + "/method-not-allowed-exception";
        MvcTestResult result = mockMvcTester.delete().uri(uri).exchange();
        assertThat(result)
                .hasStatus(METHOD_NOT_ALLOWED)
                .hasContentType(APPLICATION_PROBLEM_JSON);
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
     * @see MissingRequestValueException
     * @see MvcProblemDetailController#missingRequestValueException(String)
     */
    @Test
    void missingRequestValueExceptionTest() {
        String uri = BASE_PATH + "/missing-request-value-exception";
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
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required request param 'id' is not present.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see NotAcceptableStatusException
     * @see MvcProblemDetailController#notAcceptableStatusException()
     */
    @Test
    void notAcceptableStatusExceptionTest() {
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
     * @see PayloadTooLargeException
     * @see MvcProblemDetailController#payloadTooLargeException(MultipartFile)
     */
    @Test
    void payloadTooLargeExceptionTest() {
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
     * @see ResponseStatusException
     * @see MvcProblemDetailController#responseStatusException()
     */
    @Test
    void responseStatusExceptionTest() {
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
     * @see ServerErrorException
     * @see MvcProblemDetailController#serverErrorException()
     */
    @Test
    void serverErrorExceptionTest() {
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
     * @see ServerWebInputException
     * @see MvcProblemDetailController#serverWebInputException()
     */
    @Test
    void serverWebInputExceptionTest() {
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
     * @see UnsupportedMediaTypeStatusException
     * @see MvcProblemDetailController#unsupportedMediaTypeStatusException()
     */
    @Test
    void unsupportedMediaTypeStatusExceptionTest() {
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
        assertThat(extendedProblemDetail.getDetail()).isNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see WebExchangeBindException
     * @see MvcProblemDetailController#webExchangeBindException(ProblemDetailRequest, BindingResult)
     */
    @Test
    void webExchangeBindExceptionTest() {
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
     * @see ConversionNotSupportedException
     * @see MvcProblemDetailController#conversionNotSupportedException(String)
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
     * @see MvcProblemDetailController#typeMismatchExceptionTest()
     */
    @Test
    void typeMismatchExceptionTest() {
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
    void methodArgumentTypeMismatchExceptionTest() {
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
     */
    @Test
    void methodValidationExceptionTest() {
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
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see BusinessException
     * @see MvcProblemDetailController#businessException()
     */
    @Test
    void businessException() {
        String uri = BASE_PATH + "/business-exception";
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
                new Error("Insufficient balance"),
                new Error("Payment frequent")
        );
    }
}

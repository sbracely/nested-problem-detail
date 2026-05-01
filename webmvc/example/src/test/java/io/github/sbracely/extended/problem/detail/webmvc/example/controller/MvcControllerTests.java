package io.github.sbracely.extended.problem.detail.webmvc.example.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.webmvc.example.config.MvcMethodValidationConfiguration;
import io.github.sbracely.extended.problem.detail.webmvc.example.endpoint.MvcDemoEndpoint;
import io.github.sbracely.extended.problem.detail.webmvc.example.exception.PayFailedException;
import io.github.sbracely.extended.problem.detail.webmvc.example.request.MvcProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webmvc.example.response.serializer.MvcProblemDetailResponseSerializer;
import io.github.sbracely.extended.problem.detail.webmvc.example.service.MvcProblemDetailService;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.actuate.endpoint.web.AbstractWebMvcEndpointHandlerMapping;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.ProblemDetail;
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

@SpringBootTest
@AutoConfigureMockMvc
class MvcControllerTests {

    private static final Logger logger = LoggerFactory.getLogger(MvcControllerTests.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<Error>> ERRORS_TYPE = new TypeReference<>() {
    };
    private static final String BASE_PATH = "/mvc-extended-problem-detail";
    private static final String ZH_CN_LANGUAGE = "zh-CN";
    @Autowired
    private MockMvcTester mockMvcTester;

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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Method 'POST' is not supported.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see HttpMediaTypeNotSupportedException
     * @see MvcProblemDetailController#httpMediaTypeNotSupportedException(MvcProblemDetailRequest)
     */
    @Test
    void httpMediaTypeNotSupportedException() {
        String uri = BASE_PATH + "/http-media-type-not-supported-exception";
        MvcTestResult result = mockMvcTester.put().uri(uri).exchange();
        assertThat(result)
                .hasStatus(UNSUPPORTED_MEDIA_TYPE)
                .hasContentType(APPLICATION_PROBLEM_JSON)
                .hasHeader(ACCEPT, APPLICATION_JSON_VALUE);
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Content-Type 'null' is not supported.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see HttpMediaTypeNotAcceptableException
     * @see MvcProblemDetailController#httpMediaTypeNotAcceptableException(MvcProblemDetailRequest)
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(NOT_ACCEPTABLE.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Acceptable representations: [application/json].");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).contains("Required path variable");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Required parameter 'id' is not present.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Required part 'file' is not present.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isNull();
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        assertThat(problemDetail.getDetail()).isEqualTo("Invalid request parameters.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isNull();
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).contains("Required path parameter 'list' is not present.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Required cookie 'cookieValue' is not present.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Required header 'header' is not present.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see MethodArgumentNotValidException
     * @see MvcProblemDetailController#methodArgumentNotValidException(MvcProblemDetailRequest)
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Invalid request content.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).containsExactlyInAnyOrder(
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "list", "Maximum size is 2"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionModelAttribute(MvcProblemDetailRequest)
     * @see HandlerMethodValidationException.Visitor#modelAttribute(ModelAttribute, ParameterErrors)
     */
    @Test
    void handlerMethodValidationExceptionModelAttribute() {
        String uri = BASE_PATH + "/handler-method-validation-exception-model-attribute";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "id", "ID minimum length is 2"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see MvcProblemDetailController#handlerMethodValidationExceptionRequestBody(MvcProblemDetailRequest)
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).containsExactlyInAnyOrder(
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isEmpty();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(NOT_FOUND.value());
        assertThat(problemDetail.getDetail()).isEqualTo("No static resource %s.".formatted(uri.replaceFirst("/", "")));
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(SERVICE_UNAVAILABLE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(SERVICE_UNAVAILABLE.value());
        assertThat(problemDetail.getDetail()).isNull();
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getDetail()).isNull();
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see PayFailedException
     * @see MvcProblemDetailController#extendedErrorResponseException()
     */
    @Test
    void extendedErrorResponseException() {
        String uri = BASE_PATH + "/extended-error-response-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo("Payment failed");
        assertThat(problemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).isEqualTo("The payment request could not be processed.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).containsExactlyInAnyOrder(
                new Error(Error.Type.BUSINESS, null, "Insufficient balance"),
                new Error(Error.Type.BUSINESS, null, "Payment is too frequent")
        );
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            en,Insufficient balance,Payment is too frequent
            zh-CN,余额不足,支付过于频繁
            fr,Solde insuffisant,Le paiement est trop fréquent
            """)
    void extendedErrorResponseExceptionLocalized(String language, String firstError, String secondError) {
        String uri = BASE_PATH + "/extended-error-response-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri)
                .header(ACCEPT_LANGUAGE, language)
                .exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        assertThat(errorsOf(problemDetail)).containsExactlyInAnyOrder(
                new Error(Error.Type.BUSINESS, null, firstError),
                new Error(Error.Type.BUSINESS, null, secondError)
        );
    }

    @ParameterizedTest
    @CsvSource(
            delimiter = '|',
            nullValues = "NULL",
            textBlock = """
                    httpRequestMethodNotSupportedException|方法不被允许|不支持方法 'POST'。
                    httpMediaTypeNotSupportedException|不支持的媒体类型|不支持 Content-Type 'null'。
                    missingServletRequestParameterException|错误的请求|缺少必需参数 'id'。
                    missingRequestCookieException|错误的请求|缺少必需 Cookie 'cookieValue'。
                    missingRequestHeaderException|错误的请求|缺少必需请求头 'header'。
                    methodArgumentNotValidException|Bad Request|Invalid request content.
                    servletRequestBindingException|错误的请求|NULL
                    unsatisfiedServletRequestParameterException|错误的请求|请求参数无效。
                    orgSpringframeworkWebServerMissingRequestValueException|错误的请求|缺少必需的request param 'id'。
                    webExchangeBindException|错误的请求|请求内容无效。
                    methodNotAllowedException|方法不被允许|支持的方法：[GET, POST]
                    notAcceptableStatusException|不可接受|可接受的表示形式：[application/json]。
                    unsupportedMediaTypeStatusException|不支持的媒体类型|无法解析 Content-Type。
                    serverErrorException|服务器内部错误|服务器错误
                    payloadTooLargeException|内容过大|负载过大
                    responseStatusException|错误的请求|异常
                    serverWebInputException|错误的请求|服务器 Web 输入错误
                    httpMessageNotReadableException|错误的请求|读取请求失败
                    httpMessageNotWritableException|服务器内部错误|写入请求失败
                    methodValidationException|服务器内部错误|验证失败
                    errorResponseException|错误的请求|NULL
                    extendedErrorResponseException|支付失败|支付请求无法处理。
                    noResourceFoundException|未找到|没有静态资源 mvc-extended-problem-detail/no-resource-found-exception。
                    asyncRequestTimeoutException|服务不可用|NULL
                    contentTooLargeException|内容过大|NULL
                    """
    )
    void titleAndDetailLocalized(String scenario, String expectedTitle, String expectedDetail) throws IOException {
        ProblemDetail problemDetail = localizedScenarioResult(scenario, ZH_CN_LANGUAGE);

        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getTitle()).isEqualTo(expectedTitle);
        if (expectedDetail == null) {
            assertThat(problemDetail.getDetail()).isNull();
        } else {
            assertThat(problemDetail.getDetail()).isEqualTo(expectedDetail);
        }
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("exception");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("server web input error");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isNotNull();
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        assertThat(problemDetail.getDetail()).isEqualTo("Required request param 'id' is not present.");
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    /**
     * @see WebExchangeBindException
     * @see MvcProblemDetailController#webExchangeBindException(HttpServletRequest, MvcProblemDetailRequest, BindingResult)
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Invalid request content.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).containsExactlyInAnyOrder(
                new Error(Error.Type.PARAMETER, "name", "Name length must be between 6-10"),
                new Error(Error.Type.PARAMETER, "age", "Age cannot be null"),
                new Error(Error.Type.PARAMETER, "password", "Password and confirm password do not match"),
                new Error(Error.Type.PARAMETER, "confirmPassword", "Password and confirm password do not match")
        );
    }

    /**
     * @see MethodNotAllowedException
     * @see MvcProblemDetailController#methodNotAllowedException()
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
        assertThat(problemDetail.getDetail()).startsWith("Supported methods: [")
                .contains("GET", "POST").endsWith("]");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(NOT_ACCEPTABLE.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Acceptable representations: [application/json].");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
        assertThat(problemDetail.getDetail()).isNull();
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Could not parse Content-Type.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).isEqualTo("server error");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
        assertThat(problemDetail.getDetail()).isEqualTo("payload too large");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).contains("Failed to convert");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see MethodArgumentConversionNotSupportedException
     * @see MvcProblemDetailController#methodArgumentConversionNotSupportedException(MvcProblemDetailRequest)
     */
    @Test
    void methodArgumentConversionNotSupportedException() {
        String uri = BASE_PATH + "/method-argument-conversion-not-supported-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).param("error", "test-value").exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).contains("Failed to convert");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).contains("Failed to convert");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).contains("Failed to convert").contains("'a'");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see HttpMessageNotReadableException
     * @see MvcProblemDetailController#httpMessageNotReadableException(MvcProblemDetailRequest)
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
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Failed to read request");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see HttpMessageNotWritableException
     * @see MvcProblemDetailController#httpMessageNotWritableException()
     * @see MvcProblemDetailResponseSerializer
     */
    @Test
    void httpMessageNotWritableException() {
        String uri = BASE_PATH + "/http-message-not-writable-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Failed to write request");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see MethodValidationException
     * @see MvcProblemDetailController#methodValidationException()
     * @see MvcProblemDetailService#createProblemDetail(String, MvcProblemDetailRequest)
     * @see MvcMethodValidationConfiguration#validationPostProcessor()
     */
    @Test
    void methodValidationException() {
        String uri = BASE_PATH + "/method-validation-exception";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ProblemDetail problemDetail = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failed");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see NoHandlerFoundException
     */
    @Nested
    @TestPropertySource(properties = "spring.web.resources.add-mappings=false")
    class MvcNoHandlerFoundExceptionTest {
        @Test
        void noHandlerFoundException() {
            String uri = BASE_PATH + "/no-handler-found-exception";
            MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
            assertThat(result)
                    .hasStatus(NOT_FOUND)
                    .hasContentType(APPLICATION_PROBLEM_JSON);
            ProblemDetail problemDetail = assertThat(result).bodyJson()
                    .convertTo(ProblemDetail.class).isNotNull().actual();
            logger.info("problemDetail: " + problemDetail);
            assertThat(problemDetail.getType()).isNull();
            assertThat(problemDetail.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
            assertThat(problemDetail.getStatus()).isEqualTo(NOT_FOUND.value());
            assertThat(problemDetail.getDetail()).isEqualTo("No endpoint %s %s.".formatted(GET.name(), uri));
            assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(errorsOf(problemDetail)).isNull();
        }

        @Test
        void noHandlerFoundExceptionLocalized() {
            String uri = BASE_PATH + "/no-handler-found-exception";
            MvcTestResult result = mockMvcTester.get().uri(uri)
                    .header(ACCEPT_LANGUAGE, ZH_CN_LANGUAGE)
                    .exchange();
            ProblemDetail problemDetail = assertThat(result).bodyJson()
                    .convertTo(ProblemDetail.class).isNotNull().actual();
            assertThat(problemDetail.getTitle()).isEqualTo("未找到");
            assertThat(problemDetail.getDetail()).isEqualTo("没有端点 GET /mvc-extended-problem-detail/no-handler-found-exception。");
        }
    }

    /**
     * @see AbstractWebMvcEndpointHandlerMapping.InvalidEndpointBadRequestException
     * @see MvcDemoEndpoint#hello(String, String, String)
     */
    @Nested
    @TestPropertySource(properties = "management.endpoints.web.exposure.include=demo")
    class MvcInvalidEndpointBadRequestExceptionTests {
        private static final String BASE_PATH = "/actuator";

        @Test
        void invalidEndpointBadRequestException() {
            String uri = BASE_PATH + "/demo/name";
            MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
            assertThat(result)
                    .hasStatus(BAD_REQUEST)
                    .hasContentType(APPLICATION_PROBLEM_JSON);
            ProblemDetail problemDetail = assertThat(result).bodyJson()
                    .convertTo(ProblemDetail.class).isNotNull().actual();
            logger.info("problemDetail: " + problemDetail);
            assertThat(problemDetail.getDetail()).containsOnlyOnce("Missing parameters: ")
                    .contains("param1", "param2");
            assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(errorsOf(problemDetail)).isNull();
        }

        @Test
        void invalidEndpointBadRequestExceptionLocalized() {
            String uri = BASE_PATH + "/demo/name";
            MvcTestResult result = mockMvcTester.get().uri(uri)
                    .header(ACCEPT_LANGUAGE, ZH_CN_LANGUAGE)
                    .exchange();
            ProblemDetail problemDetail = assertThat(result).bodyJson()
                    .convertTo(ProblemDetail.class).isNotNull().actual();
            assertThat(problemDetail.getTitle()).isEqualTo("错误的请求");
            assertThat(problemDetail.getDetail()).isEqualTo("缺少参数：param1,param2");
        }
    }

    private ProblemDetail localizedScenarioResult(String scenario, String language) throws IOException {
        MvcTestResult result = switch (scenario) {
            case "httpRequestMethodNotSupportedException" ->
                    mockMvcTester.post().uri(BASE_PATH + "/http-request-method-not-supported-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "httpMediaTypeNotSupportedException" ->
                    mockMvcTester.put().uri(BASE_PATH + "/http-media-type-not-supported-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "missingServletRequestParameterException" ->
                    mockMvcTester.get().uri(BASE_PATH + "/missing-servlet-request-parameter-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "missingRequestCookieException" ->
                    mockMvcTester.get().uri(BASE_PATH + "/missing-request-cookie-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "missingRequestHeaderException" ->
                    mockMvcTester.get().uri(BASE_PATH + "/missing-request-header-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "methodArgumentNotValidException" ->
                    mockMvcTester.post().uri(BASE_PATH + "/method-argument-not-valid-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                        "name": "abc",
                                        "password": "123"
                                    }
                                    """)
                            .exchange();
            case "servletRequestBindingException" ->
                    mockMvcTester.get().uri(BASE_PATH + "/servlet-request-binding-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "unsatisfiedServletRequestParameterException" ->
                    mockMvcTester.get().uri(BASE_PATH + "/unsatisfied-servlet-request-parameter-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .param("type", "1")
                            .exchange();
            case "orgSpringframeworkWebServerMissingRequestValueException" ->
                    mockMvcTester.get().uri(BASE_PATH + "/org-springframework-web-server-missing-request-value-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "webExchangeBindException" -> mockMvcTester.post().uri(BASE_PATH + "/web-exchange-bind-exception")
                    .header(ACCEPT_LANGUAGE, language)
                    .contentType(APPLICATION_JSON)
                    .content("""
                            {
                                "name": "abc",
                                "password": "123"
                            }
                            """)
                    .exchange();
            case "methodNotAllowedException" -> mockMvcTester.delete().uri(BASE_PATH + "/method-not-allowed-exception")
                    .header(ACCEPT_LANGUAGE, language)
                    .exchange();
            case "notAcceptableStatusException" ->
                    mockMvcTester.get().uri(BASE_PATH + "/not-acceptable-status-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "unsupportedMediaTypeStatusException" ->
                    mockMvcTester.post().uri(BASE_PATH + "/unsupported-media-type-status-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "serverErrorException" -> mockMvcTester.get().uri(BASE_PATH + "/server-error-exception")
                    .header(ACCEPT_LANGUAGE, language)
                    .exchange();
            case "payloadTooLargeException" -> {
                MockMultipartFile file = new MockMultipartFile(
                        "file", "test-upload.txt", "text/plain", "test content".getBytes(StandardCharsets.UTF_8));
                yield mockMvcTester.perform(multipart(BASE_PATH + "/payload-too-large-exception")
                        .file(file)
                        .header(ACCEPT_LANGUAGE, language));
            }
            case "responseStatusException" -> mockMvcTester.get().uri(BASE_PATH + "/response-status-exception")
                    .header(ACCEPT_LANGUAGE, language)
                    .exchange();
            case "serverWebInputException" -> mockMvcTester.get().uri(BASE_PATH + "/server-web-input-exception")
                    .header(ACCEPT_LANGUAGE, language)
                    .exchange();
            case "httpMessageNotReadableException" ->
                    mockMvcTester.post().uri(BASE_PATH + "/http-message-not-readable-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                    """)
                            .exchange();
            case "httpMessageNotWritableException" ->
                    mockMvcTester.get().uri(BASE_PATH + "/http-message-not-writable-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "methodValidationException" -> mockMvcTester.get().uri(BASE_PATH + "/method-validation-exception")
                    .header(ACCEPT_LANGUAGE, language)
                    .exchange();
            case "errorResponseException" -> mockMvcTester.get().uri(BASE_PATH + "/error-response-exception")
                    .header(ACCEPT_LANGUAGE, language)
                    .exchange();
            case "extendedErrorResponseException" ->
                    mockMvcTester.get().uri(BASE_PATH + "/extended-error-response-exception")
                            .header(ACCEPT_LANGUAGE, language)
                            .exchange();
            case "noResourceFoundException" -> mockMvcTester.get().uri(BASE_PATH + "/no-resource-found-exception")
                    .header(ACCEPT_LANGUAGE, language)
                    .exchange();
            case "asyncRequestTimeoutException" -> {
                MvcTestResult mvcTestResult = mockMvcTester.get().uri(BASE_PATH + "/async-request-timeout-exception")
                        .header(ACCEPT_LANGUAGE, language)
                        .asyncExchange();
                AsyncListener listener = ((MockAsyncContext) mvcTestResult.getRequest().getAsyncContext()).getListeners().get(0);
                listener.onTimeout(null);
                yield mockMvcTester.perform(MockMvcRequestBuilders.asyncDispatch(mvcTestResult.getMvcResult()));
            }
            case "contentTooLargeException" -> {
                MockMultipartFile file = new MockMultipartFile(
                        "file", "test-upload.txt", "text/plain",
                        "Hello, this is a test file content!".getBytes(StandardCharsets.UTF_8));
                yield mockMvcTester.perform(multipart(BASE_PATH + "/content-too-large-exception")
                        .file(file)
                        .header(ACCEPT_LANGUAGE, language));
            }
            default -> throw new IllegalArgumentException("Unknown scenario: " + scenario);
        };

        return assertThat(result).bodyJson().convertTo(ProblemDetail.class).isNotNull().actual();
    }

    private static List<Error> errorsOf(ProblemDetail problemDetail) {
        if (problemDetail.getProperties() == null || problemDetail.getProperties().get("errors") == null) {
            return null;
        }
        return MAPPER.convertValue(problemDetail.getProperties().get("errors"), ERRORS_TYPE);
    }
}

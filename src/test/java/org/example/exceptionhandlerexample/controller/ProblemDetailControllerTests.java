package org.example.exceptionhandlerexample.controller;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptionhandlerexample.response.Error;
import org.example.exceptionhandlerexample.response.NestedProblemDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.net.URI;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.ALLOW;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

@Slf4j
@WebMvcTest(ProblemDetailController.class)
class ProblemDetailControllerTests {

    @Autowired
    private MockMvcTester mockMvcTester;

    private static final String BASE_PATH = "/problem-detail";

    @Test
    void httpRequestMethodNotSupportedExceptionTest() {
        String url = BASE_PATH + "/param";
        MvcTestResult result = mockMvcTester.post().uri(url).exchange();
        assertThat(result)
                .hasStatus(METHOD_NOT_ALLOWED)
                .hasContentType(APPLICATION_PROBLEM_JSON)
                .hasHeader(ALLOW, HttpMethod.GET.name());
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).contains(Arrays.asList(HttpMethod.POST.name(), "not supported"));
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00405");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
    }

    @Test
    void httpMediaTypeNotSupportedExceptionTest() {
        String url = BASE_PATH + "/consume-json";
        MvcTestResult result = mockMvcTester.put().uri(url).exchange();
        assertThat(result)
                .hasStatus(UNSUPPORTED_MEDIA_TYPE)
                .hasContentType(APPLICATION_PROBLEM_JSON)
                .hasHeader(ACCEPT, APPLICATION_JSON_VALUE);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).contains(Arrays.asList("null", "not supported"));
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00415");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
    }

    @Test
    void httpMediaTypeNotAcceptableExceptionTest() {
        String url = BASE_PATH + "/produce-json";
        MvcTestResult result = mockMvcTester.put().uri(url)
                .header(ACCEPT, APPLICATION_XML_VALUE).exchange();
        assertThat(result)
                .hasStatus(NOT_ACCEPTABLE)
                .hasContentType(APPLICATION_PROBLEM_JSON)
                .hasHeader(ACCEPT, APPLICATION_JSON_VALUE);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).contains(Arrays.asList(APPLICATION_JSON_VALUE, "Acceptable"));
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00406");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(NOT_ACCEPTABLE.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
    }

    @Test
    void missingPathVariableExceptionTest() {
        String url = BASE_PATH + "/delete/1";
        MvcTestResult result = mockMvcTester.delete().uri(url).exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).contains("Required path variable");
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00500");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @Test
    void missingServletRequestParameterExceptionTest() {
        String url = BASE_PATH + "/param";
        MvcTestResult result = mockMvcTester.get().uri(url).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).contains(Arrays.asList("id", "is not present"));
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00400");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void missingServletRequestPartExceptionTest() throws Exception {
        String url = BASE_PATH + "/file";
        MvcTestResult result = mockMvcTester.put().multipart().contentType(MULTIPART_FORM_DATA).uri(url).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).contains(Arrays.asList("file", "is not present"));
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00400");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void servletRequestBindingExceptionMissingMatrixVariableExceptionTest() {
        String url = BASE_PATH + "/matrix/abc;list1=a,b,c";
        MvcTestResult result = mockMvcTester.get().uri(url).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).contains(Arrays.asList("list", "is not present"));
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00400");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void servletRequestBindingExceptionMissingRequestCookieExceptionTest() {
        String url = BASE_PATH + "/cookie";
        MvcTestResult result = mockMvcTester.get().uri(url).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).contains(Arrays.asList("cookieValue", "is not present"));
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00400");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void servletRequestBindingExceptionMissingRequestHeaderExceptionTest() {
        String url = BASE_PATH + "/header";
        MvcTestResult result = mockMvcTester.get().uri(url).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).contains(Arrays.asList("header", "is not present"));
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00400");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void servletRequestBindingExceptionUnsatisfiedServletRequestParameterExceptionTest() {
        String url = BASE_PATH + "/unsatisfied";
        MvcTestResult result = mockMvcTester.get().uri(url).param("type", "1").exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).isEqualTo("Invalid request parameters.");
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00400");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void methodArgumentNotValidExceptionTest() {
        String url = BASE_PATH + "/create";
        MvcTestResult result = mockMvcTester.post().uri(url).contentType(APPLICATION_JSON).content("""
                                {
                                    "name": "abc",
                                    "password": "123"
                                }
                """).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).isEqualTo("Invalid request content.");
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00400");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(nestedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error("name", "姓名长度范围 6-10", Error.Type.PARAMETER),
                new Error("age", "年龄不可为空", Error.Type.PARAMETER),
                new Error("password", "密码与确认密码不一致", Error.Type.PARAMETER),
                new Error("confirmPassword", "密码与确认密码不一致", Error.Type.PARAMETER)
        );
    }

    @Test
    void handlerMethodValidationExceptionCookieValue() {
        String url = BASE_PATH + "/cookie-value";
        MvcTestResult result = mockMvcTester.get().uri(url).cookie(new Cookie("name", "a")).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00400");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(nestedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error("name", "姓名长度最小是 2", Error.Type.COOKIE));
    }

    @Test
    void handlerMethodValidationExceptionMatrixVariable() {
        String url = BASE_PATH + "/matrix-variable/abc;list=a,b,c";
        MvcTestResult result = mockMvcTester.get().uri(url).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00400");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(nestedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error("list", "最大长度是 2", Error.Type.PARAMETER));
    }

    //TODO
    @Test
    void handlerMethodValidationExceptionModelAttribute() {
        String url = BASE_PATH + "/model-attribute";
        mockMvcTester.get().uri(url).exchange();
    }

    @Test
    void handlerMethodValidationExceptionPathVariable() {
        String url = BASE_PATH + "/path-variable/a";
        MvcTestResult result = mockMvcTester.get().uri(url).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        assertThat(nestedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00400");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(url));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(nestedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error("id", "id 最小长度是 2", Error.Type.PARAMETER));
    }

}

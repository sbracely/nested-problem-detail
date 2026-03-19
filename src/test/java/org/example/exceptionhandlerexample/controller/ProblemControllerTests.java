package org.example.exceptionhandlerexample.controller;

import org.example.exceptionhandlerexample.response.ProblemDetails;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProblemController.class)
class ProblemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void httpRequestMethodNotSupportedExceptionTest() {
        String url = "/problem/param";
        MvcTestResult result = mockMvcTester.post().uri(url).exchange();
        assertThat(result)
                .hasStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .hasHeader(HttpHeaders.ALLOW, HttpMethod.GET.name());
        assertThat(result).bodyJson().convertTo(ProblemDetails.class)
                .isNotNull()
                .satisfies(problemDetails -> {
                    assertThat(problemDetails.getDetail()).contains(Arrays.asList(HttpMethod.POST.name(), "not supported"));
                    assertThat(problemDetails.getErrorCode()).isEqualTo("A00405");
                    assertThat(problemDetails.getInstance()).isEqualTo(URI.create(url));
                    assertThat(problemDetails.getStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.value());
                    assertThat(problemDetails.getTitle()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
                });
    }

    @Test
    void httpMediaTypeNotSupportedExceptionTest() throws Exception {
        String url = "/problem/consume-json";
        MvcTestResult result = mockMvcTester.put().uri(url).exchange();
        assertThat(result).hasStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .hasHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        assertThat(result).bodyJson().convertTo(ProblemDetails.class)
                .isNotNull()
                .satisfies(problemDetails -> {
                    assertThat(problemDetails.getDetail()).contains(Arrays.asList("null", "not supported"));
                    assertThat(problemDetails.getErrorCode()).isEqualTo("A00415");
                    assertThat(problemDetails.getInstance()).isEqualTo(URI.create(url));
                    assertThat(problemDetails.getStatus()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
                    assertThat(problemDetails.getTitle()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
                });
    }

    @Test
    void httpMediaTypeNotAcceptableExceptionTest() throws Exception {
        String url = "/problem/produce-json";
        mockMvc.perform(MockMvcRequestBuilders.put(url).header("Accept", MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(header().string("Accept", Matchers.is(MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(jsonPath("$.detail").value(allOf(
                        containsString(MediaType.APPLICATION_JSON_VALUE),
                        containsString("Acceptable")
                )))
                .andExpect(jsonPath("$.errorCode").value("A00406"))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(NOT_ACCEPTABLE.value()))
                .andExpect(jsonPath("$.title").value(NOT_ACCEPTABLE.getReasonPhrase()));
    }

    @Test
    void missingPathVariableExceptionTest() throws Exception {
        String url = "/problem/delete/1";
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(Matchers.containsString("path variable")))
                .andExpect(jsonPath("$.errorCode").value("A00500"))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.title").value(INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }

    @Test
    void missingServletRequestParameterExceptionTest() throws Exception {
        String url = "/problem/param";
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(Matchers.allOf(
                        Matchers.containsString("id"),
                        Matchers.containsString("is not present")
                )))
                .andExpect(jsonPath("$.errorCode").value("A00400"))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()));
    }

    @Test
    void missingServletRequestPartExceptionTest() throws Exception {
        String url = "/problem/file";
        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, url)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(Matchers.allOf(
                        Matchers.containsString("file"),
                        Matchers.containsString("not present")
                )))
                .andExpect(jsonPath("$.errorCode").value("A00400"))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()));
    }

    @Test
    void servletRequestBindingExceptionMissingMatrixVariableExceptionTest() throws Exception {
        String url = "/problem/matrix/abc;list1=a,b,c";
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(Matchers.allOf(
                        Matchers.containsString("list"),
                        Matchers.containsString("is not present")
                )))
                .andExpect(jsonPath("$.errorCode").value("A00400"))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()));
    }

    @Test
    void servletRequestBindingExceptionMissingRequestCookieExceptionTest() throws Exception {
        String url = "/problem/cookie";
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(Matchers.allOf(
                        Matchers.containsString("cookieValue"),
                        Matchers.containsString("is not present")
                )))
                .andExpect(jsonPath("$.errorCode").value("A00400"))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()));
    }

    @Test
    void servletRequestBindingExceptionMissingRequestHeaderExceptionTest() throws Exception {
        String url = "/problem/header";
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(Matchers.allOf(
                        Matchers.containsString("header"),
                        Matchers.containsString("is not present")
                )))
                .andExpect(jsonPath("$.errorCode").value("A00400"))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()));
    }

    @Test
    void servletRequestBindingExceptionUnsatisfiedServletRequestParameterExceptionTest() throws Exception {
        String url = "/problem/unsatisfied";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("type", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(Matchers.is("Invalid request parameters.")))
                .andExpect(jsonPath("$.errorCode").value("A00400"))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()));
    }

    @Test
    void methodArgumentNotValidExceptionTest() throws Exception {
        String url = "/problem/create";
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                    "name": "abc",
                                    "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(Matchers.is("Invalid request content.")))
                .andExpect(jsonPath("$.errorCode").value("A00400"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").value(Matchers.hasSize(3)))
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].message")
                        .value(hasItem("姓名长度范围 6-10")))
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].type")
                        .value(hasItem("parameter")))
                .andExpect(jsonPath("$.errors[?(@.field == 'age')].message")
                        .value(hasItem("年龄不可为空")))
                .andExpect(jsonPath("$.errors[?(@.field == 'age')].type")
                        .value(hasItem("parameter")))
                .andExpect(jsonPath("$.errors[?(@.message == '密码与确认密码不一致')]").value(hasSize(1)))
                .andExpect(jsonPath("$.errors[?(@.message == '密码与确认密码不一致')].type").value("parameter"))
                .andExpect(jsonPath("$.errors[?(@.message == '密码与确认密码不一致')].field").value(hasItem(nullValue())))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST.getReasonPhrase()));
    }
}

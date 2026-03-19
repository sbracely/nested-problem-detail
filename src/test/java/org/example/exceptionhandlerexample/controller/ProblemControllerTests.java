package org.example.exceptionhandlerexample.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProblemControllerTests {

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private MockMvcTester mockMvcTester;

    @Test
    void httpRequestMethodNotSupportedExceptionTest() throws Exception {
        String url = "/problem/param";
        mockMvc.perform(MockMvcRequestBuilders.post(url))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(header().string("Allow", containsString(HttpMethod.GET.name())))
                .andExpect(jsonPath("$.detail").value(allOf(
                        containsString(HttpMethod.POST.name()),
                        containsString("not supported")
                )))
                .andExpect(jsonPath("$.errorCode").value("A00405"))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(METHOD_NOT_ALLOWED.value()))
                .andExpect(jsonPath("$.title").value(METHOD_NOT_ALLOWED.getReasonPhrase()));
    }

    @Test
    void httpMediaTypeNotSupportedExceptionTest() throws Exception {
        String url = "/problem/consume-json";
        mockMvc.perform(MockMvcRequestBuilders.put(url))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(header().string("Accept", Matchers.is(MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(jsonPath("$.detail").value(allOf(
                        containsString("null"),
                        containsString("not supported")
                )))
                .andExpect(jsonPath("$.errorCode").value("A00415"))
                .andExpect(jsonPath("$.instance").value(url))
                .andExpect(jsonPath("$.status").value(UNSUPPORTED_MEDIA_TYPE.value()))
                .andExpect(jsonPath("$.title").value(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase()));
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
        String url = "/problem/1";
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

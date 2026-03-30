package com.github.sbracely.extended.problem.detail.test.flux.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
class ExtendProblemDetailFluxTests {


    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_PATH = "/mvc-problem-detail";

    @Test
    void httpRequestMethodNotSupportedException() {
//        String uri = BASE_PATH + "/param";
//        MvcTestResult result = webTestClient.post().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(METHOD_NOT_ALLOWED)
//                .hasContentType(APPLICATION_PROBLEM_JSON)
//                .hasHeader(ALLOW, HttpMethod.GET.name());
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).contains(Arrays.asList(HttpMethod.POST.name(), "not supported"));
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
    }

    @Test
    void httpMediaTypeNotSupportedException() {
//        String uri = BASE_PATH + "/consume-json";
//        MvcTestResult result = webTestClient.put().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(UNSUPPORTED_MEDIA_TYPE)
//                .hasContentType(APPLICATION_PROBLEM_JSON)
//                .hasHeader(ACCEPT, APPLICATION_JSON_VALUE);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).contains(Arrays.asList("null", "not supported"));
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
    }

    @Test
    void httpMediaTypeNotAcceptableException() {
//        String uri = BASE_PATH + "/produce-json";
//        MvcTestResult result = webTestClient.put().uri(uri)
//                .header(ACCEPT, APPLICATION_XML_VALUE).exchange();
//        assertThat(result)
//                .hasStatus(NOT_ACCEPTABLE)
//                .hasContentType(APPLICATION_PROBLEM_JSON)
//                .hasHeader(ACCEPT, APPLICATION_JSON_VALUE);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).contains(Arrays.asList(APPLICATION_JSON_VALUE, "Acceptable"));
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_ACCEPTABLE.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
    }

    @Test
    void missingPathVariableException() {
//        String uri = BASE_PATH + "/delete/1";
//        MvcTestResult result = webTestClient.delete().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(INTERNAL_SERVER_ERROR)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).contains("Required path variable");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @Test
    void missingServletRequestParameterException() {
//        String uri = BASE_PATH + "/param";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).contains(Arrays.asList("id", "is not present"));
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void missingServletRequestPartException() {
//        String uri = BASE_PATH + "/file";
//        MvcTestResult result = webTestClient.put().multipart().contentType(MULTIPART_FORM_DATA).uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).contains(Arrays.asList("file", "is not present"));
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void servletRequestBindingException() {
//        String uri = BASE_PATH + "/servlet-request-binding";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isNull();
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void servletRequestBindingExceptionMissingMatrixVariableException() {
//        String uri = BASE_PATH + "/matrix/abc;list1=a,b,c";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).contains(Arrays.asList("list", "is not present"));
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void servletRequestBindingExceptionMissingRequestCookieException() {
//        String uri = BASE_PATH + "/cookie";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).contains(Arrays.asList("cookieValue", "is not present"));
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void servletRequestBindingExceptionMissingRequestHeaderException() {
//        String uri = BASE_PATH + "/header";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).contains(Arrays.asList("header", "is not present"));
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void servletRequestBindingExceptionMissingRequestValueException() {
//        String uri = BASE_PATH + "/missing-request-value-mvc";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isNull();
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void servletRequestBindingExceptionUnsatisfiedServletRequestParameterException() {
//        String uri = BASE_PATH + "/unsatisfied";
//        MvcTestResult result = webTestClient.get().uri(uri).param("type", "1").exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid request parameters.");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void methodArgumentNotValidException() {
//        String uri = BASE_PATH + "/create";
//        MvcTestResult result = webTestClient.post().uri(uri).contentType(APPLICATION_JSON).content("""
//                                {
//                                    "name": "abc",
//                                    "password": "123"
//                                }
//                """).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid request content.");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
//                new Error(Error.Type.PARAMETER, "name", "姓名长度范围 6-10"),
//                new Error(Error.Type.PARAMETER, "age", "年龄不可为空"),
//                new Error(Error.Type.PARAMETER, "password", "密码与确认密码不一致"),
//                new Error(Error.Type.PARAMETER, "confirmPassword", "密码与确认密码不一致")
//        );
    }

    @Test
    void handlerMethodValidationExceptionCookieValue() {
//        String uri = BASE_PATH + "/cookie-value";
//        MvcTestResult result = webTestClient.get().uri(uri).cookie(new Cookie("name", "a")).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).singleElement()
//                .isEqualTo(new Error(Error.Type.COOKIE, "name", "姓名长度最小是 2"));
    }

    @Test
    void handlerMethodValidationExceptionMatrixVariable() {
//        String uri = BASE_PATH + "/matrix-variable/abc;list=a,b,c";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).singleElement()
//                .isEqualTo(new Error(Error.Type.PARAMETER, "list", "最大长度是 2"));
    }

    @Test
    void handlerMethodValidationExceptionModelAttribute() {
//        String uri = BASE_PATH + "/model-attribute";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).singleElement()
//                .isEqualTo(new Error(Error.Type.PARAMETER, "password", "密码不能是空"));
    }

    @Test
    void handlerMethodValidationExceptionPathVariable() {
//        String uri = BASE_PATH + "/path-variable/a";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).singleElement()
//                .isEqualTo(new Error(Error.Type.PARAMETER, "id", "id 最小长度是 2"));
    }

    @Test
    void handlerMethodValidationExceptionRequestBody() {
//        String uri = BASE_PATH + "/request-body";
//        MvcTestResult result = webTestClient.post().uri(uri).content("""
//                {
//                    "name": "abc"
//                }
//                """).contentType(APPLICATION_JSON).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).singleElement()
//                .isEqualTo(new Error(Error.Type.PARAMETER, "password", "密码不能是空"));
    }

    @Test
    void handlerMethodValidationExceptionHeader() {
//        String uri = BASE_PATH + "/request-header";
//        MvcTestResult result = webTestClient.get().uri(uri).header("headerValue", "a").exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).singleElement()
//                .isEqualTo(new Error(Error.Type.HEADER, "headerValue", "最小长度是 2"));
    }

    @Test
    void handlerMethodValidationExceptionRequestParam() {
//        String uri = BASE_PATH + "/request-param";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
//                new Error(Error.Type.PARAMETER, "param", "参数不能为空"),
//                new Error(Error.Type.PARAMETER, "param2", "参数2不能为空"),
//                new Error(Error.Type.PARAMETER, "param2", "参数2不能为null")
//        );
    }

    @Test
    void handlerMethodValidationExceptionRequestPart() {
//        String uri = BASE_PATH + "/request-part";
//        MvcTestResult result = webTestClient.get().uri(uri)
//                .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data").exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).singleElement()
//                .isEqualTo(new Error(Error.Type.PARAMETER, "file", "文件不能为空"));
    }

    @Test
    void handlerMethodValidationExceptionOther() {
//        String uri = BASE_PATH + "/request-other";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void handlerMethodValidationExceptionRequestBodyValidationResult() {
//        String uri = BASE_PATH + "/request-body-validation-result";
//        MvcTestResult result = webTestClient.post().uri(uri).contentType(APPLICATION_JSON).content("""
//                              ["","a"]
//                """).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).singleElement()
//                .isEqualTo(new Error(Error.Type.PARAMETER, null, "元素不能包含空"));

    }

    @Nested
    @TestPropertySource(properties = "spring.web.resources.add-mappings=false")
    class NoHandlerFoundExceptionTest {
        @Test
        void noHandlerFoundException() {
//            String uri = BASE_PATH + "/no-handler-found";
//            MvcTestResult result = webTestClient.get().uri(uri).exchange();
//            assertThat(result)
//                    .hasStatus(NOT_FOUND)
//                    .hasContentType(APPLICATION_PROBLEM_JSON);
//            ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                    .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//            assertThat(extendedProblemDetail.getDetail()).contains("No endpoint");
//            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//            assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_FOUND.value());
//            assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
//            assertThat(extendedProblemDetail.getErrors()).isNull();
        }
    }

    @Test
    void noResourceFoundException() {
//        String uri = BASE_PATH + "/no-resource-found";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(NOT_FOUND)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).contains("No static resource");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_FOUND.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void asyncRequestTimeoutException() throws IOException {
//        String uri = BASE_PATH + "/async-request-timeout";
//        MvcTestResult mvcTestResult = webTestClient.get().uri(uri).asyncExchange();
//        assertThat(mvcTestResult.getRequest().isAsyncStarted()).isTrue();
//        AsyncContext asyncContext = mvcTestResult.getRequest().getAsyncContext();
//        assertThat(asyncContext).isNotNull();
//        AsyncListener listener = ((MockAsyncContext) asyncContext).getListeners().get(0);
//        listener.onTimeout(null);
//        MvcTestResult result = webTestClient.perform(MockMvcRequestBuilders.asyncDispatch(mvcTestResult.getMvcResult()));
//        assertThat(result)
//                .hasStatus(SERVICE_UNAVAILABLE)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isNull();
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(SERVICE_UNAVAILABLE.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(SERVICE_UNAVAILABLE.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void errorResponseException() {
//        String uri = BASE_PATH + "/error-response-exception";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isNull();
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void errorResponseExceptionContentTooLargeException() {
//        String uri = BASE_PATH + "/content-too-large";
//        MockMultipartFile file = new MockMultipartFile("file", "test-upload.txt",
//                "text/plain", "Hello, this is a test file content!".getBytes(StandardCharsets.UTF_8));
//        MvcTestResult result = webTestClient.perform(multipart(uri).file(file));
//        assertThat(result)
//                .hasStatus(CONTENT_TOO_LARGE)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isNull();
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Nested
    @TestPropertySource(properties = "spring.mvc.apiversion.use.header=API-Version")
    class ApiVersionTest {
        @Test
        void errorResponseExceptionInvalidApiVersionException() {
//            String uri = BASE_PATH + "/api-version";
//            MvcTestResult result = webTestClient.get().uri(uri)
//                    .header("API-Version", "1").exchange();
//            assertThat(result)
//                    .hasStatus(BAD_REQUEST)
//                    .hasContentType(APPLICATION_PROBLEM_JSON);
//            ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                    .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//            log.info("extendedProblemDetail: {}", extendedProblemDetail);
//            assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid API version: '1.0.0'.");
//            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        }

        @Test
        void errorResponseExceptionMissingApiVersionException() {
//            String uri = BASE_PATH + "/api-version";
//            MvcTestResult result = webTestClient.get().uri(uri).exchange();
//            assertThat(result)
//                    .hasStatus(BAD_REQUEST)
//                    .hasContentType(APPLICATION_PROBLEM_JSON);
//            ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                    .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//            log.info("extendedProblemDetail: {}", extendedProblemDetail);
//            assertThat(extendedProblemDetail.getDetail()).isEqualTo("API version is required.");
//            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        }
    }

    @Nested
    @TestPropertySource(properties = "management.endpoints.web.exposure.include=demo")
    class EndPointTest {
        private static final String BASE_PATH = "/actuator";

        @Test
        void errorResponseExceptionAbstractWebMvcEndpointHandlerMappingInvalidEndpointBadRequestException() {
//            String uri = BASE_PATH + "/demo/name";
//            MvcTestResult result = webTestClient.get().uri(uri).exchange();
//            assertThat(result)
//                    .hasStatus(BAD_REQUEST)
//                    .hasContentType(APPLICATION_PROBLEM_JSON);
//            ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                    .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//            log.info("extendedProblemDetail: {}", extendedProblemDetail);
//            assertThat(extendedProblemDetail.getDetail()).containsOnlyOnce("Missing parameters: ")
//                    .contains("param1", "param2");
//            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//            assertThat(extendedProblemDetail.getErrors()).isNull();
        }
    }

    @Test
    void errorResponseExceptionMethodNotAllowedException() {
//        String uri = BASE_PATH + "/method-not-allowed";
//        MvcTestResult result = webTestClient.delete().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(METHOD_NOT_ALLOWED)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).startsWith("Supported methods: [")
//                .contains("GET", "POST").endsWith("]");
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo("Method Not Allowed");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
    }

    @Test
    void errorResponseExceptionMissRequestValueException() {
//        String uri = BASE_PATH + "/missing-request-value";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required request param 'id' is not present.");
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void errorResponseExceptionNotAcceptableStatusException() {
//        String uri = BASE_PATH + "/not-acceptable-status";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(NOT_ACCEPTABLE)
//                .hasContentType(APPLICATION_PROBLEM_JSON)
//                .hasHeader(ACCEPT, APPLICATION_JSON_VALUE);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Acceptable representations: [application/json].");
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_ACCEPTABLE.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
    }

    @Test
    void errorResponseExceptionPayloadTooLargeException() {
//        String uri = BASE_PATH + "/payload-too-large";
//        MockMultipartFile file = new MockMultipartFile("file", "test-upload.txt",
//                "text/plain", "test content".getBytes(StandardCharsets.UTF_8));
//        MvcTestResult result = webTestClient.perform(multipart(uri).file(file));
//        assertThat(result)
//                .hasStatus(CONTENT_TOO_LARGE)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("payload too large");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void errorResponseResponseStatusExceptionBaseClass() {
//        String uri = BASE_PATH + "/response-status-exception";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("exception");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void errorResponseExceptionServerErrorException() {
//        String uri = BASE_PATH + "/server-error";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(INTERNAL_SERVER_ERROR)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("server error");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void errorResponseExceptionServerWebInputException() {
//        String uri = BASE_PATH + "/server-web-input";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("server web input error");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void errorResponseExceptionUnsatisfiedRequestParameterException() {
//        String uri = BASE_PATH + "/unsatisfied-request-param";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isNotNull();
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void errorResponseExceptionUnsupportedMediaTypeStatusException() {
//        String uri = BASE_PATH + "/unsupported-media-type";
//        MvcTestResult result = webTestClient.post().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(UNSUPPORTED_MEDIA_TYPE)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Could not parse Content-Type.");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void errorResponseExceptionWebExchangeBindException() {
//        String uri = BASE_PATH + "/web-exchange-bind";
//        MvcTestResult result = webTestClient.post().uri(uri).contentType(APPLICATION_JSON).content("""
//                                {
//                                    "name": "abc",
//                                    "password": "123"
//                                }
//                """).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid request content.");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
//                new Error(Error.Type.PARAMETER, "name", "姓名长度范围 6-10"),
//                new Error(Error.Type.PARAMETER, "age", "年龄不可为空"),
//                new Error(Error.Type.PARAMETER, "password", "密码与确认密码不一致"),
//                new Error(Error.Type.PARAMETER, "confirmPassword", "密码与确认密码不一致")
//        );
    }


    @Test
    void conversionNotSupportedException() {
//        String uri = BASE_PATH + "/conversion-not-supported";
//        MvcTestResult result = webTestClient.get().uri(uri).param("data", "test-value").exchange();
//        assertThat(result)
//                .hasStatus(INTERNAL_SERVER_ERROR)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        // TODO custom detail
//        assertThat(extendedProblemDetail.getDetail()).contains("Failed to convert 'null' with value: 'test-value'");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void conversionNotSupportedExceptionMethodArgumentConversionNotSupportedException() {
//        String uri = BASE_PATH + "/method-argument-conversion-not-supported";
//        MvcTestResult result = webTestClient.get().uri(uri).param("error", "test-value").exchange();
//        assertThat(result)
//                .hasStatus(INTERNAL_SERVER_ERROR)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).contains("Failed to convert");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void typeMismatchException() {
//        String uri = BASE_PATH + "/type-mismatch-exception";
//        MvcTestResult result = webTestClient.get().uri(uri).param("integer", "a").exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        // TODO custom detail
//        assertThat(extendedProblemDetail.getDetail()).contains("Failed to convert 'null' with value: 'test'");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void typeMismatchExceptionMethodArgumentTypeMismatchException() {
//        String uri = BASE_PATH + "/method-argument-type-mismatch";
//        MvcTestResult result = webTestClient.get().uri(uri).param("integer", "a").exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).contains("Failed to convert").contains("'a'");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void httpMessageNotReadableException() {
//        String uri = BASE_PATH + "/http-message-not-readable";
//        MvcTestResult result = webTestClient.post().uri(uri).contentType(APPLICATION_JSON).content("""
//                           {
//                """).exchange();
//        assertThat(result)
//                .hasStatus(BAD_REQUEST)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Failed to read request");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void httpMessageNotWritableException() {
//        String uri = BASE_PATH + "/http-message-not-writable";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(INTERNAL_SERVER_ERROR)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Failed to write request");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void methodValidationException() {
//        String uri = BASE_PATH + "/method-validation";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(INTERNAL_SERVER_ERROR)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failed");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void customized() {
//        String uri = BASE_PATH + "/customized";
//        MvcTestResult result = webTestClient.get().uri(uri).exchange();
//        assertThat(result)
//                .hasStatus(INTERNAL_SERVER_ERROR)
//                .hasContentType(APPLICATION_PROBLEM_JSON);
//        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
//                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
//        log.info("extendedProblemDetail: {}", extendedProblemDetail);
//        assertThat(extendedProblemDetail.getDetail()).isEqualTo("支付失败");
//        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
//        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
//        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
//                new Error("余额不足"),
//                new Error("支付频繁")
//        );
    }
}

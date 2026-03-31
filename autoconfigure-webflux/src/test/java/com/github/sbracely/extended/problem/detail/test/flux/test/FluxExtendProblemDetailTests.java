package com.github.sbracely.extended.problem.detail.test.flux.test;

import com.github.sbracely.extended.problem.detail.response.Error;
import com.github.sbracely.extended.problem.detail.response.ExtendedProblemDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.ALLOW;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT10M")
class FluxExtendProblemDetailTests {

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_PATH = "/extend-problem-detail-flux";

    @Test
    void methodNotAllowedException() {
        String uri = BASE_PATH + "/method-not-allowed";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.delete().uri(uri).exchange()
                .expectStatus().isEqualTo(METHOD_NOT_ALLOWED)
                .expectHeader().valueEquals(ALLOW, GET.name())
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Supported methods: [GET]");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void notAcceptableStatusException() {
        String uri = BASE_PATH + "/not-acceptable-status";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .header(ACCEPT, APPLICATION_XML_VALUE)
                .exchange()
                .expectStatus().isEqualTo(NOT_ACCEPTABLE)
                .expectHeader().valueEquals(ACCEPT, APPLICATION_JSON_VALUE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_ACCEPTABLE.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Acceptable representations: [application/json].");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void unsupportedMediaTypeStatusException() {
        String uri = BASE_PATH + "/unsupported-media-type";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.post().uri(uri).exchange()
                .expectStatus().isEqualTo(UNSUPPORTED_MEDIA_TYPE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectHeader().valueEquals(ACCEPT, APPLICATION_XML_VALUE)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(extendedProblemDetail.getDetail()).isNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void missingRequestValueException() {
        String uri = BASE_PATH + "/missing-request-value";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri).exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required query parameter 'id' is not present.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void unsatisfiedRequestParameterException() {
        String uri = BASE_PATH + "/unsatisfied-request-param";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isNotNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void webExchangeBindException() {
        String uri = BASE_PATH + "/web-exchange-bind";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.post().uri(uri)
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "abc",
                            "password": "123"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid request content.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.PARAMETER, "name", "姓名长度范围 6-10"),
                new Error(Error.Type.PARAMETER, "age", "年龄不可为空"),
                new Error(Error.Type.PARAMETER, "password", "密码与确认密码不一致"),
                new Error(Error.Type.PARAMETER, "confirmPassword", "密码与确认密码不一致")
        );
    }

    @Test
    void handlerMethodValidationExceptionCookie() {
        String uri = BASE_PATH + "/handler-method-validation-cookie";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .cookie("cookieValue", "")
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.COOKIE, "cookieValue", "cookie不能为空"));
    }

    @Test
    void handlerMethodValidationExceptionMatrixVariable() {
        String uri = BASE_PATH + "/handler-method-validation-matrix/abc;list=a,b,c";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "list", "list最大长度是2"));
    }

    @Test
    void handlerMethodValidationExceptionModelAttribute() {
        String uri = BASE_PATH + "/handler-method-validation-model";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "password", "密码不能是空"));
    }

    @Test
    void handlerMethodValidationExceptionPathVariable() {
        String uri = BASE_PATH + "/handler-method-validation-path/abc";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "id", "id长度至少5"));
    }

    @Test
    void handlerMethodValidationExceptionRequestBody() {
        String uri = BASE_PATH + "/handler-method-validation-body";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.post().uri(uri)
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "abc"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "password", "密码不能是空"));
    }

    @Test
    void handlerMethodValidationExceptionHeader() {
        String uri = BASE_PATH + "/handler-method-validation-header";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .header("headerValue", "")
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.HEADER, "headerValue", "header不能为空"));
    }

    @Test
    void handlerMethodValidationExceptionRequestParam() {
        String uri = BASE_PATH + "/handler-method-validation-request-param";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParam("param", "")
                        .queryParam("value", "ab")
                        .build())
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.PARAMETER, "param", "参数不能为空"),
                new Error(Error.Type.PARAMETER, "value", "长度至少5")
        );
    }

    @Test
    void handlerMethodValidationExceptionRequestPart() {
        String uri = BASE_PATH + "/handler-method-validation-request-part";
        // 测试文件为空的情况 - 使用 body() 方法让 WebTestClient 自动添加 boundary
        ExtendedProblemDetail extendedProblemDetail = webTestClient.post().uri(uri)
                .bodyValue(Collections.emptyMap())
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "file", "文件不能为空"));
    }

    @Test
    void handlerMethodValidationExceptionRequestBodyValidationResult() {
        String uri = BASE_PATH + "/handler-method-validation-request-body-validation-result";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.post().uri(uri)
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        ["", "a"]
                        """)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, null, "元素不能包含空"));
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void handlerMethodValidationExceptionOther(CapturedOutput output) {
        String uri = BASE_PATH + "/handler-method-validation-other";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
        assertThat(output.getOut()).contains(Arrays.asList(
                "codes: [NotBlank.extendProblemDetailFluxController#handlerMethodValidationOther.sessionAttribute, NotBlank.sessionAttribute, NotBlank.java.lang.String, NotBlank], defaultMessage: sessionAttribute 不能为空",
                "codes: [NotBlank.extendProblemDetailFluxController#handlerMethodValidationOther.requestAttribute, NotBlank.requestAttribute, NotBlank.java.lang.String, NotBlank], defaultMessage: requestAttribute 不能为空",
                "codes: [NotBlank.extendProblemDetailFluxController#handlerMethodValidationOther.value, NotBlank.value, NotBlank.java.lang.String, NotBlank], defaultMessage: value 不能为空"
        ));
    }


    @Test
    void serverWebInputException() {
        String uri = BASE_PATH + "/server-web-input";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("server web input error");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void serverErrorException() {
        String uri = BASE_PATH + "/server-error";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("server error");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void responseStatusException() {
        String uri = BASE_PATH + "/response-status-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("exception");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void responseStatusExceptionContentTooLargeException() {
        String uri = BASE_PATH + "/content-too-large";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.post().uri(uri)
                .bodyValue("x".repeat(1024 * 1024)) // 1MB
                .exchange()
                .expectStatus().isEqualTo(CONTENT_TOO_LARGE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
        assertThat(extendedProblemDetail.getDetail()).isNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Nested
    @TestPropertySource(properties = {
            "spring.webflux.apiversion.use.header=API-Version",
            "spring.webflux.apiversion.supported=1,2",
    })
    @AutoConfigureWebTestClient(timeout = "PT10M")
    @Import(ApiVersionTests.NotAcceptableApiVersionController.class)
    class ApiVersionTests {

        @Test
        void responseStatusExceptionInvalidApiVersionException() {
            String uri = BASE_PATH + "/response-status-exception-invalid-api-version";
            ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                    .header("API-Version", "3")
                    .exchange()
                    .expectStatus().isEqualTo(BAD_REQUEST)
                    .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            log.info("extendedProblemDetail: {}", extendedProblemDetail);
            assertThat(extendedProblemDetail).isNotNull();
            assertThat(extendedProblemDetail.getType()).isNull();
            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid API version: '3.0.0'.");
            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(extendedProblemDetail.getProperties()).isNull();
            assertThat(extendedProblemDetail.getErrors()).isNull();
        }

        @Test
        void responseStatusExceptionMissingApiVersionException() {
            String uri = BASE_PATH + "/response-status-exception-missing-api-version";
            EntityExchangeResult<ExtendedProblemDetail> result = webTestClient.get()
                    .uri(uri)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(BAD_REQUEST)
                    .expectHeader()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult();
            ExtendedProblemDetail extendedProblemDetail = result.getResponseBody();
            log.info("extendedProblemDetail: {}", extendedProblemDetail);
            assertThat(extendedProblemDetail).isNotNull();
            assertThat(extendedProblemDetail.getDetail()).isEqualTo("API version is required.");
            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        }

        @RestController
        static class NotAcceptableApiVersionController {
            @GetMapping(value = "/response-status-exception-not-acceptable-api-version", version = "1")
            public Mono<Void> responseStatusExceptionNotAcceptableApiVersion() {
                log.info("response status exception not acceptable api version");
                return Mono.empty();
            }
        }


        @Test
        void responseStatusExceptionNotAcceptableApiVersionException() {
            String uri = "/response-status-exception-not-acceptable-api-version";
            ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                    .header("API-Version", "2")
                    .exchange()
                    .expectStatus().isEqualTo(BAD_REQUEST)
                    .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            log.info("extendedProblemDetail: {}", extendedProblemDetail);
            assertThat(extendedProblemDetail).isNotNull();
            assertThat(extendedProblemDetail.getType()).isNull();
            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid API version: '2.0.0'.");
            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(extendedProblemDetail.getProperties()).isNull();
            assertThat(extendedProblemDetail.getErrors()).isNull();
        }
    }

    @Nested
    @TestPropertySource(properties = "management.endpoints.web.exposure.include=demo")
    class EndPointTests {

        private static final String BASE_PATH = "/actuator";

        @Test
        void responseStatusExceptionInvalidEndpointBadRequestException() {
            String uri = BASE_PATH + "/demo/name";
            ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri).exchange()
                    .expectStatus().isEqualTo(BAD_REQUEST)
                    .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            log.info("extendedProblemDetail: {}", extendedProblemDetail);
            assertThat(extendedProblemDetail).isNotNull();
            assertThat(extendedProblemDetail.getType()).isNull();
            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(extendedProblemDetail.getDetail()).containsOnlyOnce("Missing parameters: ")
                    .contains("param1", "param2");
            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(extendedProblemDetail.getProperties()).isNull();
            assertThat(extendedProblemDetail.getErrors()).isNull();
        }
    }

    @Test
    void noResourceFoundException() {
        String uri = BASE_PATH + "/no-resource-found/nonexistent.js";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(NOT_FOUND)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_FOUND.value());
        assertThat(extendedProblemDetail.getDetail()).contains("No static resource");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }














































    @Test
    void errorResponseExceptionNotAcceptableApiVersionException() {
        String uri = BASE_PATH + "/api-version-test";
        EntityExchangeResult<ExtendedProblemDetail> result = webTestClient.get()
                .uri(uri)
                .header("API-Version", "2")
                .exchange()
                .expectStatus()
                .isEqualTo(BAD_REQUEST)
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult();
        ExtendedProblemDetail extendedProblemDetail = result.getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid API version: '2.0.0'.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }

    @Test
    void errorResponseExceptionMissingApiVersionException() {
        String uri = BASE_PATH + "/api-version-test";
        EntityExchangeResult<ExtendedProblemDetail> result = webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isEqualTo(BAD_REQUEST)
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult();
        ExtendedProblemDetail extendedProblemDetail = result.getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("API version is required.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    }


    @Test
    void errorResponseException() {
        String uri = BASE_PATH + "/error-response-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("error response exception");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void methodValidationException() {
        String uri = BASE_PATH + "/method-validation";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failed");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    @Test
    void customizedException() {
        String uri = BASE_PATH + "/customized";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("支付失败");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error("余额不足"),
                new Error("支付频繁")
        );
    }


    @Test
    void missingApiVersionException() {
        String uri = BASE_PATH + "/missing-api-version";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isNotEmpty();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }


    @Test
    void payloadTooLargeException() {
        String uri = BASE_PATH + "/payload-too-large";
        // 通过发送过大的请求体触发 PayloadTooLargeException
        ExtendedProblemDetail extendedProblemDetail = webTestClient.post().uri(uri)
                .bodyValue("x".repeat(1024 * 1024)) // 1MB 数据
                .exchange()
                .expectStatus().isEqualTo(PAYLOAD_TOO_LARGE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(PAYLOAD_TOO_LARGE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(PAYLOAD_TOO_LARGE.value());
        assertThat(extendedProblemDetail.getDetail()).isNotEmpty();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }
}

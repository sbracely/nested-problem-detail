package io.github.sbracely.extended.problem.detail.webflux.example.controller;

import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import io.github.sbracely.extended.problem.detail.webflux.example.config.FluxMethodValidationConfiguration;
import io.github.sbracely.extended.problem.detail.webflux.example.exception.PayFailedException;
import io.github.sbracely.extended.problem.detail.webflux.example.request.FluxProblemDetailRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.ALLOW;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT1M")
class FluxControllerTests {

    private static final Logger logger = LoggerFactory.getLogger(FluxControllerTests.class);
    private static final String BASE_PATH = "/flux-extended-problem-detail";
    private static final String ZH_CN_LANGUAGE = "zh-CN";

    @BeforeAll
    static void useEnglishLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Autowired
    private WebTestClient webTestClient;

    /**
     * @see MethodNotAllowedException
     * @see FluxExtendedProblemDetailController#methodNotAllowedException()
     */
    @Test
    void methodNotAllowedException() {
        String uri = BASE_PATH + "/method-not-allowed-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.delete().uri(uri).exchange()
                .expectStatus().isEqualTo(METHOD_NOT_ALLOWED)
                .expectHeader().valueEquals(ALLOW, GET.name())
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Supported methods: [GET]");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see NotAcceptableStatusException
     * @see FluxExtendedProblemDetailController#notAcceptableStatusException()
     */
    @Test
    void notAcceptableStatusException() {
        String uri = BASE_PATH + "/not-acceptable-status-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .header(ACCEPT, APPLICATION_XML_VALUE)
                .exchange()
                .expectStatus().isEqualTo(NOT_ACCEPTABLE)
                .expectHeader().valueEquals(ACCEPT, APPLICATION_JSON_VALUE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_ACCEPTABLE.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Acceptable representations: [application/json].");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see UnsupportedMediaTypeStatusException
     * @see FluxExtendedProblemDetailController#unsupportedMediaTypeStatusException()
     */
    @Test
    void unsupportedMediaTypeStatusException() {
        String uri = BASE_PATH + "/unsupported-media-type-status-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.post().uri(uri).exchange()
                .expectStatus().isEqualTo(UNSUPPORTED_MEDIA_TYPE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectHeader().valueEquals(ACCEPT, APPLICATION_XML_VALUE)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(extendedProblemDetail.getDetail()).isNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see MissingRequestValueException
     * @see FluxExtendedProblemDetailController#missingRequestValueException(String)
     */
    @Test
    void missingRequestValueException() {
        String uri = BASE_PATH + "/missing-request-value-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri).exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Required query parameter 'id' is not present.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see UnsatisfiedRequestParameterException
     * @see FluxExtendedProblemDetailController#unsatisfiedRequestParameterException()
     */
    @Test
    void unsatisfiedRequestParameterException() {
        String uri = BASE_PATH + "/unsatisfied-request-parameter-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isNotNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see WebExchangeBindException
     * @see FluxExtendedProblemDetailController#webExchangeBindException(FluxProblemDetailRequest)
     */
    @Test
    void webExchangeBindException() {
        String uri = BASE_PATH + "/web-exchange-bind-exception";
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
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid request content.");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.PARAMETER, "name", "Name length must be between 6-10"),
                new Error(Error.Type.PARAMETER, "age", "Age cannot be null"),
                new Error(Error.Type.PARAMETER, "password", "Password and confirm password do not match"),
                new Error(Error.Type.PARAMETER, "confirmPassword", "Password and confirm password do not match")
        );
    }

    /**
     * @see HandlerMethodValidationException
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionCookieValue(String)
     * @see HandlerMethodValidationException.Visitor#cookieValue(CookieValue, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionCookieValue() {
        String uri = BASE_PATH + "/handler-method-validation-exception-cookie-value";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .cookie("cookieValue", "")
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.COOKIE, "cookieValue", "cookie cannot be empty"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionMatrix(String, List)
     * @see HandlerMethodValidationException.Visitor#matrixVariable(MatrixVariable, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionMatrix() {
        String uri = BASE_PATH + "/handler-method-validation-exception-matrix/abc;list=a,b,c";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "list", "list maximum size is 2"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionModelAttribute(FluxProblemDetailRequest)
     * @see HandlerMethodValidationException.Visitor#modelAttribute(ModelAttribute, ParameterErrors)
     */
    @Test
    void handlerMethodValidationExceptionModelAttribute() {
        String uri = BASE_PATH + "/handler-method-validation-exception-model-attribute";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
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
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionPathVariable(String)
     * @see HandlerMethodValidationException.Visitor#pathVariable(PathVariable, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionPathVariable() {
        String uri = BASE_PATH + "/handler-method-validation-exception-path-variable/abc";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.PARAMETER, "id", "id length must be at least 5"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionRequestBody(FluxProblemDetailRequest)
     * @see HandlerMethodValidationException.Visitor#requestBody(RequestBody, ParameterErrors)
     */
    @Test
    void handlerMethodValidationExceptionRequestBody() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-body";
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
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
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
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionRequestBodyValidationResult(List)
     * @see HandlerMethodValidationException.Visitor#requestBodyValidationResult(RequestBody, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionRequestBodyValidationResult() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-body-validation-result";
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
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
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
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionRequestHeader(String)
     * @see HandlerMethodValidationException.Visitor#requestHeader(RequestHeader, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionRequestHeader() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-header";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .header("headerValue", "")
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).singleElement()
                .isEqualTo(new Error(Error.Type.HEADER, "headerValue", "Header cannot be empty"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionRequestParam(String, String)
     * @see HandlerMethodValidationException.Visitor#requestParam(RequestParam, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionRequestParam() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-param";
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
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.PARAMETER, "param", "Parameter cannot be empty"),
                new Error(Error.Type.PARAMETER, "value", "Length must be at least 5")
        );
    }

    /**
     * @see HandlerMethodValidationException
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionRequestPart(FilePart)
     * @see HandlerMethodValidationException.Visitor#requestPart(RequestPart, ParameterErrors)
     */
    @Test
    void handlerMethodValidationExceptionRequestPart() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-part";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.post().uri(uri)
                .bodyValue(Collections.emptyMap())
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
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
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionOther(String, String, String)
     * @see HandlerMethodValidationException.Visitor#other(ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionOther() {
        String uri = BASE_PATH + "/handler-method-validation-exception-other";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
//        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);
//        verify(extendedProblemDetailLog, atLeastOnce()).log(any(), isNull(), eq("codes: {}, defaultMessage: {}"), argsCaptor.capture());
//        List<String> defaultMessages = argsCaptor.getAllValues().stream()
//                .map(args -> (String) args[1])
//                .toList();
//        assertThat(defaultMessages).containsExactlyInAnyOrder(
//                "sessionAttribute cannot be empty",
//                "requestAttribute cannot be empty",
//                "value cannot be empty"
//        );
    }

    /**
     * @see ServerWebInputException
     * @see FluxExtendedProblemDetailController#serverWebInputException()
     */
    @Test
    void serverWebInputException() {
        String uri = BASE_PATH + "/server-web-input-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("server web input error");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see ServerErrorException
     * @see FluxExtendedProblemDetailController#serverErrorException()
     */
    @Test
    void serverErrorException() {
        String uri = BASE_PATH + "/server-error-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("server error");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see ResponseStatusException
     * @see FluxExtendedProblemDetailController#responseStatusException()
     */
    @Test
    void responseStatusException() {
        String uri = BASE_PATH + "/response-status-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("exception");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see NoResourceFoundException
     */
    @Test
    void noResourceFoundException() {
        String uri = BASE_PATH + "/no-resource-found";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(NOT_FOUND)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(NOT_FOUND.value());
        assertThat(extendedProblemDetail.getDetail()).contains("No static resource");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see PayloadTooLargeException
     * @see FluxExtendedProblemDetailController#payloadTooLargeException(byte[])
     */
    @Test
    void payloadTooLargeException() {
        String uri = BASE_PATH + "/payload-too-large-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.post().uri(uri)
                .bodyValue("text")
                .exchange()
                .expectStatus().isEqualTo(PAYLOAD_TOO_LARGE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo("Content Too Large");
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(PAYLOAD_TOO_LARGE.value());
        assertThat(extendedProblemDetail.getDetail()).isNull();
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    /**
     * @see ErrorResponseException
     * @see FluxExtendedProblemDetailController#errorResponseException()
     */
    @Test
    void errorResponseException() {
        String uri = BASE_PATH + "/error-response-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo("Error title");
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Error details");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.BUSINESS, null, "Error message 1"),
                new Error(Error.Type.BUSINESS, null, "Error message 2")
        );
    }

    /**
     * @see PayFailedException
     * @see FluxExtendedProblemDetailController#extendedErrorResponseException()
     */
    @Test
    void extendedErrorResponseException() {
        String uri = BASE_PATH + "/extended-error-response-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo("Payment failed");
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("The payment request could not be processed.");
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
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
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .header("Accept-Language", language)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getErrors()).containsExactlyInAnyOrder(
                new Error(Error.Type.BUSINESS, null, firstError),
                new Error(Error.Type.BUSINESS, null, secondError)
        );
    }

    @ParameterizedTest
    @CsvSource(
            delimiter = '|',
            nullValues = "NULL",
            textBlock = """
                    methodNotAllowedException|方法不被允许|支持的方法：[GET]
                    notAcceptableStatusException|不可接受|可接受的表示形式：[application/json]。
                    missingRequestValueException|错误的请求|缺少必需的query parameter 'id'。
                    webExchangeBindException|Bad Request|Invalid request content.
                    handlerMethodValidationExceptionRequestParam|Bad Request|Validation failure
                    serverWebInputException|错误的请求|服务器 Web 输入错误
                    serverErrorException|服务器内部错误|服务器错误
                    responseStatusException|错误的请求|异常
                    contentTooLargeException|内容过大|NULL
                    noResourceFoundException|未找到|没有静态资源 {0}。
                    payloadTooLargeException|内容过大|NULL
                    errorResponseException|错误标题|错误详情
                    extendedErrorResponseException|支付失败|支付请求无法处理。
                    methodValidationException|服务器内部错误|验证失败
                    """
    )
    void titleAndDetailLocalized(String scenario, String expectedTitle, String expectedDetail) {
        ExtendedProblemDetail extendedProblemDetail = localizedScenarioResult(scenario, ZH_CN_LANGUAGE);

        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(expectedTitle);
        if (expectedDetail == null) {
            assertThat(extendedProblemDetail.getDetail()).isNull();
        } else {
            assertThat(extendedProblemDetail.getDetail()).isEqualTo(expectedDetail);
        }
    }

    /**
     * @see MethodValidationException
     * @see FluxExtendedProblemDetailController#methodValidationException()
     * @see FluxMethodValidationConfiguration#validationPostProcessor()
     */
    @Test
    void methodValidationException() {
        String uri = BASE_PATH + "/method-validation-exception";
        ExtendedProblemDetail extendedProblemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("extendedProblemDetail: " + extendedProblemDetail);
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failed");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }

    private ExtendedProblemDetail localizedScenarioResult(String scenario, String language) {
        return switch (scenario) {
            case "methodNotAllowedException" -> webTestClient.delete()
                    .uri(BASE_PATH + "/method-not-allowed-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "notAcceptableStatusException" -> webTestClient.get()
                    .uri(BASE_PATH + "/not-acceptable-status-exception")
                    .header("Accept-Language", language)
                    .header(ACCEPT, APPLICATION_XML_VALUE)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "missingRequestValueException" -> webTestClient.get()
                    .uri(BASE_PATH + "/missing-request-value-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "webExchangeBindException" -> webTestClient.post()
                    .uri(BASE_PATH + "/web-exchange-bind-exception")
                    .header("Accept-Language", language)
                    .contentType(APPLICATION_JSON)
                    .bodyValue("""
                            {
                                "name": "abc",
                                "password": "123"
                            }
                            """)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "handlerMethodValidationExceptionRequestParam" -> webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(BASE_PATH + "/handler-method-validation-exception-request-param")
                            .queryParam("param", "")
                            .queryParam("value", "ab")
                            .build())
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "serverWebInputException" -> webTestClient.get()
                    .uri(BASE_PATH + "/server-web-input-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "serverErrorException" -> webTestClient.get()
                    .uri(BASE_PATH + "/server-error-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "responseStatusException" -> webTestClient.get()
                    .uri(BASE_PATH + "/response-status-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "contentTooLargeException" -> webTestClient.post()
                    .uri(BASE_PATH + "/content-too-large-exception")
                    .header("Accept-Language", language)
                    .bodyValue("x".repeat(1024 * 1024))
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "noResourceFoundException" -> webTestClient.get()
                    .uri(BASE_PATH + "/no-resource-found")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "payloadTooLargeException" -> webTestClient.post()
                    .uri(BASE_PATH + "/payload-too-large-exception")
                    .header("Accept-Language", language)
                    .bodyValue("text")
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "errorResponseException" -> webTestClient.get()
                    .uri(BASE_PATH + "/error-response-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "extendedErrorResponseException" -> webTestClient.get()
                    .uri(BASE_PATH + "/extended-error-response-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            case "methodValidationException" -> webTestClient.get()
                    .uri(BASE_PATH + "/method-validation-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ExtendedProblemDetail.class)
                    .returnResult().getResponseBody();
            default -> throw new IllegalArgumentException("Unknown scenario: " + scenario);
        };
    }
}

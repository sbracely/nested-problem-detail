package io.github.sbracely.extended.problem.detail.webflux.example.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sbracely.extended.problem.detail.common.response.Error;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.accept.InvalidApiVersionException;
import org.springframework.web.accept.MissingApiVersionException;
import org.springframework.web.accept.NotAcceptableApiVersionException;
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
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<Error>> ERRORS_TYPE = new TypeReference<>() {
    };
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
        ProblemDetail problemDetail = webTestClient.delete().uri(uri).exchange()
                .expectStatus().isEqualTo(METHOD_NOT_ALLOWED)
                .expectHeader().valueEquals(ALLOW, GET.name())
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Supported methods: [GET]");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see NotAcceptableStatusException
     * @see FluxExtendedProblemDetailController#notAcceptableStatusException()
     */
    @Test
    void notAcceptableStatusException() {
        String uri = BASE_PATH + "/not-acceptable-status-exception";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .header(ACCEPT, APPLICATION_XML_VALUE)
                .exchange()
                .expectStatus().isEqualTo(NOT_ACCEPTABLE)
                .expectHeader().valueEquals(ACCEPT, APPLICATION_JSON_VALUE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(NOT_ACCEPTABLE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(NOT_ACCEPTABLE.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Acceptable representations: [application/json].");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see UnsupportedMediaTypeStatusException
     * @see FluxExtendedProblemDetailController#unsupportedMediaTypeStatusException()
     */
    @Test
    void unsupportedMediaTypeStatusException() {
        String uri = BASE_PATH + "/unsupported-media-type-status-exception";
        ProblemDetail problemDetail = webTestClient.post().uri(uri).exchange()
                .expectStatus().isEqualTo(UNSUPPORTED_MEDIA_TYPE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectHeader().valueEquals(ACCEPT, APPLICATION_XML_VALUE)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(problemDetail.getDetail()).isNull();
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see MissingRequestValueException
     * @see FluxExtendedProblemDetailController#missingRequestValueException(String)
     */
    @Test
    void missingRequestValueException() {
        String uri = BASE_PATH + "/missing-request-value-exception";
        ProblemDetail problemDetail = webTestClient.get().uri(uri).exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Required query parameter 'id' is not present.");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see UnsatisfiedRequestParameterException
     * @see FluxExtendedProblemDetailController#unsatisfiedRequestParameterException()
     */
    @Test
    void unsatisfiedRequestParameterException() {
        String uri = BASE_PATH + "/unsatisfied-request-parameter-exception";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isNotNull();
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see WebExchangeBindException
     * @see FluxExtendedProblemDetailController#webExchangeBindException(FluxProblemDetailRequest)
     */
    @Test
    void webExchangeBindException() {
        String uri = BASE_PATH + "/web-exchange-bind-exception";
        ProblemDetail problemDetail = webTestClient.post().uri(uri)
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
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
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
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionCookieValue(String)
     * @see HandlerMethodValidationException.Visitor#cookieValue(CookieValue, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionCookieValue() {
        String uri = BASE_PATH + "/handler-method-validation-exception-cookie-value";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .cookie("cookieValue", "")
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
                .isEqualTo(new Error(Error.Type.COOKIE, "cookieValue", "cookie cannot be empty"));
    }

    /**
     * @see HandlerMethodValidationException
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionMatrixVariable(String, List)
     * @see HandlerMethodValidationException.Visitor#matrixVariable(MatrixVariable, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionMatrixVariable() {
        String uri = BASE_PATH + "/handler-method-validation-exception-matrix/abc;list=a,b,c";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
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
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
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
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionPathVariable(String)
     * @see HandlerMethodValidationException.Visitor#pathVariable(PathVariable, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionPathVariable() {
        String uri = BASE_PATH + "/handler-method-validation-exception-path-variable/abc";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
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
        ProblemDetail problemDetail = webTestClient.post().uri(uri)
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "abc"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
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
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionRequestBodyValidationResult(List)
     * @see HandlerMethodValidationException.Visitor#requestBodyValidationResult(RequestBody, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionRequestBodyValidationResult() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-body-validation-result";
        ProblemDetail problemDetail = webTestClient.post().uri(uri)
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        ["", "a"]
                        """)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
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
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionRequestHeader(String)
     * @see HandlerMethodValidationException.Visitor#requestHeader(RequestHeader, ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionRequestHeader() {
        String uri = BASE_PATH + "/handler-method-validation-exception-request-header";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .header("headerValue", "")
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).singleElement()
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
        ProblemDetail problemDetail = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParam("param", "")
                        .queryParam("value", "ab")
                        .build())
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).containsExactlyInAnyOrder(
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
        ProblemDetail problemDetail = webTestClient.post().uri(uri)
                .bodyValue(Collections.emptyMap())
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
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
     * @see FluxExtendedProblemDetailController#handlerMethodValidationExceptionOther(String, String, String)
     * @see HandlerMethodValidationException.Visitor#other(ParameterValidationResult)
     */
    @Test
    void handlerMethodValidationExceptionOther() {
        String uri = BASE_PATH + "/handler-method-validation-exception-other";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failure");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isEmpty();
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
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("server web input error");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see ServerErrorException
     * @see FluxExtendedProblemDetailController#serverErrorException()
     */
    @Test
    void serverErrorException() {
        String uri = BASE_PATH + "/server-error-exception";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).isEqualTo("server error");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see ResponseStatusException
     * @see FluxExtendedProblemDetailController#responseStatusException()
     */
    @Test
    void responseStatusException() {
        String uri = BASE_PATH + "/response-status-exception";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("exception");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see ContentTooLargeException
     * @see FluxExtendedProblemDetailController#contentTooLargeException(byte[])
     */
    @Test
    void contentTooLargeException() {
        String uri = BASE_PATH + "/content-too-large-exception";
        ProblemDetail problemDetail = webTestClient.post().uri(uri)
                .bodyValue("x".repeat(1024 * 1024)) // 1MB
                .exchange()
                .expectStatus().isEqualTo(CONTENT_TOO_LARGE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
        assertThat(problemDetail.getDetail()).isNull();
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see NoResourceFoundException
     */
    @Test
    void noResourceFoundException() {
        String uri = BASE_PATH + "/no-resource-found";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(NOT_FOUND)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(NOT_FOUND.value());
        assertThat(problemDetail.getDetail()).contains("No static resource");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see PayloadTooLargeException
     * @see FluxExtendedProblemDetailController#payloadTooLargeException(byte[])
     */
    @Test
    void payloadTooLargeException() {
        String uri = BASE_PATH + "/payload-too-large-exception";
        ProblemDetail problemDetail = webTestClient.post().uri(uri)
                .bodyValue("text")
                .exchange()
                .expectStatus().isEqualTo(PAYLOAD_TOO_LARGE)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(PAYLOAD_TOO_LARGE.value());
        assertThat(problemDetail.getDetail()).isNull();
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * @see ErrorResponseException
     * @see FluxExtendedProblemDetailController#errorResponseException()
     */
    @Test
    void errorResponseException() {
        String uri = BASE_PATH + "/error-response-exception";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo("Error title");
        assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Error details");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).containsExactlyInAnyOrder(
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
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo("Payment failed");
        assertThat(problemDetail.getDetail()).isEqualTo("The payment request could not be processed.");
        assertThat(problemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
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
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .header("Accept-Language", language)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        assertThat(problemDetail).isNotNull();
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
                    methodNotAllowedException|方法不被允许|支持的方法：[GET]
                    notAcceptableStatusException|不可接受|可接受的表示形式：[application/json]。
                    missingRequestValueException|错误的请求|缺少必需的query parameter 'id'。
                    webExchangeBindException|Bad Request|Invalid request content.
                    handlerMethodValidationExceptionRequestParam|Bad Request|Validation failure
                    serverWebInputException|错误的请求|服务器 Web 输入错误
                    serverErrorException|服务器内部错误|服务器错误
                    responseStatusException|错误的请求|异常
                    contentTooLargeException|内容过大|NULL
                    noResourceFoundException|未找到|没有静态资源 flux-extended-problem-detail/no-resource-found。
                    payloadTooLargeException|内容过大|NULL
                    errorResponseException|错误标题|错误详情
                    extendedErrorResponseException|支付失败|支付请求无法处理。
                    methodValidationException|服务器内部错误|验证失败
                    """
    )
    void titleAndDetailLocalized(String scenario, String expectedTitle, String expectedDetail) {
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
     * @see MethodValidationException
     * @see FluxExtendedProblemDetailController#methodValidationException()
     * @see FluxMethodValidationConfiguration#validationPostProcessor()
     */
    @Test
    void methodValidationException() {
        String uri = BASE_PATH + "/method-validation-exception";
        ProblemDetail problemDetail = webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();
        logger.info("problemDetail: " + problemDetail);
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getType()).isNull();
        assertThat(problemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(problemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).isEqualTo("Validation failed");
        assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(errorsOf(problemDetail)).isNull();
    }

    /**
     * {@link InvalidApiVersionException}
     * {@link MissingApiVersionException}
     * {@link NotAcceptableApiVersionException}
     */
    @Nested
    @TestPropertySource(properties = {
            "spring.webflux.apiversion.use.header=API-Version",
            "spring.webflux.apiversion.supported=1,2",
    })
    @AutoConfigureWebTestClient(timeout = "PT1M")
    class FluxApiVersionTests {

        /**
         * @see InvalidApiVersionException
         * @see FluxExtendedProblemDetailController#invalidApiVersionException()
         */
        @Test
        void invalidApiVersionException() {
            String uri = BASE_PATH + "/invalid-api-version-exception";
            ProblemDetail problemDetail = webTestClient.get().uri(uri)
                    .header("API-Version", "3")
                    .exchange()
                    .expectStatus().isEqualTo(BAD_REQUEST)
                    .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            logger.info("problemDetail: " + problemDetail);
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getType()).isNull();
            assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(problemDetail.getDetail()).isEqualTo("Invalid API version: '3.0.0'.");
            assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(errorsOf(problemDetail)).isNull();
        }

        @Test
        void invalidApiVersionExceptionLocalized() {
            String uri = BASE_PATH + "/invalid-api-version-exception";
            ProblemDetail problemDetail = webTestClient.get().uri(uri)
                    .header("API-Version", "3")
                    .header("Accept-Language", ZH_CN_LANGUAGE)
                    .exchange()
                    .expectStatus().isEqualTo(BAD_REQUEST)
                    .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getTitle()).isEqualTo("错误的请求");
            assertThat(problemDetail.getDetail()).isEqualTo("无效的 API 版本：'3.0.0'。");
        }

        /**
         * @see MissingApiVersionException
         * @see FluxExtendedProblemDetailController#missingApiVersionException()
         */
        @Test
        void missingApiVersionException() {
            String uri = BASE_PATH + "/missing-api-version-exception";
            EntityExchangeResult<ProblemDetail> result = webTestClient.get()
                    .uri(uri)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(BAD_REQUEST)
                    .expectHeader()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .expectBody(ProblemDetail.class)
                    .returnResult();
            ProblemDetail problemDetail = result.getResponseBody();
            logger.info("problemDetail: " + problemDetail);
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getDetail()).isEqualTo("API version is required.");
            assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        }

        @Test
        void missingApiVersionExceptionLocalized() {
            String uri = BASE_PATH + "/missing-api-version-exception";
            EntityExchangeResult<ProblemDetail> result = webTestClient.get()
                    .uri(uri)
                    .header("Accept-Language", ZH_CN_LANGUAGE)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(BAD_REQUEST)
                    .expectHeader()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .expectBody(ProblemDetail.class)
                    .returnResult();
            ProblemDetail problemDetail = result.getResponseBody();
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getTitle()).isEqualTo("错误的请求");
            assertThat(problemDetail.getDetail()).isEqualTo("必须提供 API 版本。");
        }

        /**
         * @see NotAcceptableApiVersionException
         * @see FluxApiVersionController#notAcceptableApiVersion()
         */
        @Test
        void notAcceptableApiVersionException() {
            String uri = "/not-acceptable-api-version";
            ProblemDetail problemDetail = webTestClient.get().uri(uri)
                    .header("API-Version", "2")
                    .exchange()
                    .expectStatus().isEqualTo(BAD_REQUEST)
                    .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            logger.info("problemDetail: " + problemDetail);
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getType()).isNull();
            assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(problemDetail.getDetail()).isEqualTo("Invalid API version: '2.0.0'.");
            assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(errorsOf(problemDetail)).isNull();
        }

        @Test
        void notAcceptableApiVersionExceptionLocalized() {
            String uri = "/not-acceptable-api-version";
            ProblemDetail problemDetail = webTestClient.get().uri(uri)
                    .header("API-Version", "2")
                    .header("Accept-Language", ZH_CN_LANGUAGE)
                    .exchange()
                    .expectStatus().isEqualTo(BAD_REQUEST)
                    .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getTitle()).isEqualTo("错误的请求");
            assertThat(problemDetail.getDetail()).isEqualTo("无效的 API 版本：'2.0.0'。");
        }

    }

    private ProblemDetail localizedScenarioResult(String scenario, String language) {
        return switch (scenario) {
            case "methodNotAllowedException" -> webTestClient.delete()
                    .uri(BASE_PATH + "/method-not-allowed-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "notAcceptableStatusException" -> webTestClient.get()
                    .uri(BASE_PATH + "/not-acceptable-status-exception")
                    .header("Accept-Language", language)
                    .header(ACCEPT, APPLICATION_XML_VALUE)
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "missingRequestValueException" -> webTestClient.get()
                    .uri(BASE_PATH + "/missing-request-value-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ProblemDetail.class)
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
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "handlerMethodValidationExceptionRequestParam" -> webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(BASE_PATH + "/handler-method-validation-exception-request-param")
                            .queryParam("param", "")
                            .queryParam("value", "ab")
                            .build())
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "serverWebInputException" -> webTestClient.get()
                    .uri(BASE_PATH + "/server-web-input-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "serverErrorException" -> webTestClient.get()
                    .uri(BASE_PATH + "/server-error-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "responseStatusException" -> webTestClient.get()
                    .uri(BASE_PATH + "/response-status-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "contentTooLargeException" -> webTestClient.post()
                    .uri(BASE_PATH + "/content-too-large-exception")
                    .header("Accept-Language", language)
                    .bodyValue("x".repeat(1024 * 1024))
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "noResourceFoundException" -> webTestClient.get()
                    .uri(BASE_PATH + "/no-resource-found")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "payloadTooLargeException" -> webTestClient.post()
                    .uri(BASE_PATH + "/payload-too-large-exception")
                    .header("Accept-Language", language)
                    .bodyValue("text")
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "errorResponseException" -> webTestClient.get()
                    .uri(BASE_PATH + "/error-response-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "extendedErrorResponseException" -> webTestClient.get()
                    .uri(BASE_PATH + "/extended-error-response-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            case "methodValidationException" -> webTestClient.get()
                    .uri(BASE_PATH + "/method-validation-exception")
                    .header("Accept-Language", language)
                    .exchange()
                    .expectBody(ProblemDetail.class)
                    .returnResult().getResponseBody();
            default -> throw new IllegalArgumentException("Unknown scenario: " + scenario);
        };
    }

    private static List<Error> errorsOf(ProblemDetail problemDetail) {
        if (problemDetail.getProperties() == null || problemDetail.getProperties().get("errors") == null) {
            return null;
        }
        return MAPPER.convertValue(problemDetail.getProperties().get("errors"), ERRORS_TYPE);
    }
}

package io.github.sbracely.extended.problem.detail.webmvc.example.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.accept.InvalidApiVersionException;
import org.springframework.web.accept.MissingApiVersionException;
import org.springframework.web.accept.NotAcceptableApiVersionException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONTENT_TOO_LARGE;

@AutoConfigureTestRestTemplate
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MvcControllerRandomPortTests {

    private static final Logger logger = LoggerFactory.getLogger(MvcControllerRandomPortTests.class);
    private static final String BASE_PATH = "/mvc-extended-problem-detail";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String ZH_CN_LANGUAGE = "zh-CN";
    @LocalServerPort
    private int port;

    /**
     * @see AsyncRequestNotUsableException
     * @see MvcProblemDetailController#asyncRequestNotUsableException()
     */
    @Test
    void asyncRequestNotUsableException() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofMillis(1));

        RestClient restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl("http://localhost:" + port)
                .build();
        assertThatThrownBy(() -> restClient.get()
                .uri(BASE_PATH + "/async-request-not-usable-exception")
                .retrieve()
                .toBodilessEntity())
                .isExactlyInstanceOf(ResourceAccessException.class)
                .hasCauseInstanceOf(SocketTimeoutException.class)
                .hasMessageContaining("Read timed out");
    }

    /**
     * @see MaxUploadSizeExceededException
     */
    @Nested
    @TestPropertySource(properties = "spring.servlet.multipart.max-file-size=1")
    class MvcMaxUploadSizeExceededExceptionTests {

        @Autowired
        private TestRestTemplate testRestTemplate;

        /**
         * @see MaxUploadSizeExceededException
         * @see MvcProblemDetailController#maxUploadSizeExceededException(MultipartFile)
         */
        @Test
        void maxUploadSizeExceededException() {
            byte[] largeContent = new byte[2];

            ByteArrayResource resource = new ByteArrayResource(largeContent) {
                @Override
                public String getFilename() {
                    return "large-test-file.txt";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setAcceptLanguageAsLocales(java.util.List.of(java.util.Locale.ENGLISH));

            String uri = BASE_PATH + "/max-upload-size-exceeded-exception";
            ResponseEntity<ProblemDetail> response = testRestTemplate.postForEntity(
                    "http://localhost:" + port + uri,
                    new HttpEntity<>(body, headers),
                    ProblemDetail.class
            );
            assertThat(response.getStatusCode()).isEqualTo(CONTENT_TOO_LARGE);
            assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
            ProblemDetail problemDetail = response.getBody();
            logger.info("problemDetail: " + problemDetail);
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getType()).isNull();
            assertThat(problemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
            assertThat(problemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
            assertThat(problemDetail.getDetail()).isEqualTo("Maximum upload size exceeded");
            assertThat(problemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(errorsOf(problemDetail)).isNull();
        }

        @Test
        void maxUploadSizeExceededExceptionLocalized() {
            byte[] largeContent = new byte[2];

            ByteArrayResource resource = new ByteArrayResource(largeContent) {
                @Override
                public String getFilename() {
                    return "large-test-file.txt";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setAcceptLanguageAsLocales(java.util.List.of(java.util.Locale.SIMPLIFIED_CHINESE));

            String uri = BASE_PATH + "/max-upload-size-exceeded-exception";
            ResponseEntity<ProblemDetail> response = testRestTemplate.postForEntity(
                    "http://localhost:" + port + uri,
                    new HttpEntity<>(body, headers),
                    ProblemDetail.class
            );
            ProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getTitle()).isEqualTo("内容过大");
            assertThat(problemDetail.getDetail()).isEqualTo("超过最大上传大小");
        }
    }

    /**
     * {@link InvalidApiVersionException}
     * {@link MissingApiVersionException}
     * {@link NotAcceptableApiVersionException}
     */
    @Nested
    @AutoConfigureRestTestClient
    @TestPropertySource(properties = {
            "spring.mvc.apiversion.use.header=API-Version",
            "spring.mvc.apiversion.supported=1,2",
    })
    class MvcApiVersionTests {

        @Autowired
        private RestTestClient restTestClient;

        /**
         * @see InvalidApiVersionException
         * @see MvcProblemDetailController#invalidApiVersionException()
         */
        @Test
        void invalidApiVersionException() {
            String uri = "http://localhost:" + port + BASE_PATH + "/invalid-api-version-exception";
            EntityExchangeResult<ProblemDetail> result = restTestClient.get()
                    .uri(uri)
                    .header("API-Version", "3")
                    .header("Accept-Language", DEFAULT_LANGUAGE)
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
            assertThat(problemDetail.getType()).isNull();
            assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(problemDetail.getDetail()).isEqualTo("Invalid API version: '3.0.0'.");
            assertThat(problemDetail.getInstance()).isEqualTo(URI.create("/mvc-extended-problem-detail/invalid-api-version-exception"));
            assertThat(errorsOf(problemDetail)).isNull();
        }

        @Test
        void invalidApiVersionExceptionLocalized() {
            String uri = "http://localhost:" + port + BASE_PATH + "/invalid-api-version-exception";
            EntityExchangeResult<ProblemDetail> result = restTestClient.get()
                    .uri(uri)
                    .header("API-Version", "3")
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
            assertThat(problemDetail.getDetail()).isEqualTo("无效的 API 版本：'3.0.0'。");
        }

        /**
         * @see NotAcceptableApiVersionException
         * @see MvcApiVersionController#notAcceptableApiVersion()
         */
        @Test
        void notAcceptableApiVersionException() {
            String uri = "http://localhost:" + port + "/not-acceptable-api-version";
            EntityExchangeResult<ProblemDetail> result = restTestClient.get()
                    .uri(uri)
                    .header("API-Version", "2")
                    .header("Accept-Language", DEFAULT_LANGUAGE)
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
            assertThat(problemDetail.getType()).isNull();
            assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(problemDetail.getDetail()).isEqualTo("Invalid API version: '2.0.0'.");
            assertThat(problemDetail.getInstance()).isEqualTo(URI.create("/not-acceptable-api-version"));
            assertThat(errorsOf(problemDetail)).isNull();
        }

        @Test
        void notAcceptableApiVersionExceptionLocalized() {
            String uri = "http://localhost:" + port + "/not-acceptable-api-version";
            EntityExchangeResult<ProblemDetail> result = restTestClient.get()
                    .uri(uri)
                    .header("API-Version", "2")
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
            assertThat(problemDetail.getDetail()).isEqualTo("无效的 API 版本：'2.0.0'。");
        }

        /**
         * @see MissingApiVersionException
         * @see MvcProblemDetailController#missingApiVersionException()
         */
        @Test
        void missingApiVersionException() {
            String uri = "http://localhost:" + port + BASE_PATH + "/missing-api-version-exception";
            EntityExchangeResult<ProblemDetail> result = restTestClient.get()
                    .uri(uri)
                    .header("Accept-Language", DEFAULT_LANGUAGE)
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
            assertThat(problemDetail.getType()).isNull();
            assertThat(problemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(problemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(problemDetail.getDetail()).isEqualTo("API version is required.");
            assertThat(problemDetail.getInstance()).isEqualTo(URI.create("/mvc-extended-problem-detail/missing-api-version-exception"));
            assertThat(errorsOf(problemDetail)).isNull();
        }

        @Test
        void missingApiVersionExceptionLocalized() {
            String uri = "http://localhost:" + port + BASE_PATH + "/missing-api-version-exception";
            EntityExchangeResult<ProblemDetail> result = restTestClient.get()
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
    }

    private static Object errorsOf(ProblemDetail problemDetail) {
        return problemDetail.getProperties() == null ? null : problemDetail.getProperties().get("errors");
    }
}

package com.github.sbracely.extended.problem.detail.test.mvc.test;

import com.github.sbracely.extended.problem.detail.core.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.test.mvc.controller.MvcProblemDetailController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONTENT_TOO_LARGE;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MvcExtendedProblemDetailRandomPortTests {

    @LocalServerPort
    private int port;

    private static final String BASE_PATH = "/mvc-extended-problem-detail";

    /**
     * @see MaxUploadSizeExceededException
     */
    @Nested
    @AutoConfigureTestRestTemplate
    @TestPropertySource(properties = "spring.servlet.multipart.max-file-size=1")
    class MaxUploadSizeExceededExceptionTests {

        @Autowired
        private TestRestTemplate restTemplate;

        /**
         * @see MaxUploadSizeExceededException
         * @see MvcProblemDetailController#maxUploadSizeExceedededException(MultipartFile)
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

            String uri = BASE_PATH + "/max-upload-size-exceeded";
            ResponseEntity<ExtendedProblemDetail> response = restTemplate.postForEntity(
                    "http://localhost:" + port + uri,
                    new HttpEntity<>(body, headers),
                    ExtendedProblemDetail.class
            );
            assertThat(response.getStatusCode()).isEqualTo(CONTENT_TOO_LARGE);
            assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
            ExtendedProblemDetail extendedProblemDetail = response.getBody();
            log.info("extendedProblemDetail: {}", extendedProblemDetail);
            assertThat(extendedProblemDetail).isNotNull();
            assertThat(extendedProblemDetail.getType()).isNull();
            assertThat(extendedProblemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
            assertThat(extendedProblemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
            assertThat(extendedProblemDetail.getDetail()).isEqualTo("Maximum upload size exceeded");
            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
            assertThat(extendedProblemDetail.getProperties()).isNull();
            assertThat(extendedProblemDetail.getErrors()).isNull();
        }
    }

    /**
     * {@link InvalidApiVersionException}
     * {@link MissingApiVersionException}
     * {@link NotAcceptableApiVersionException}
     */
    @Nested
    @AutoConfigureRestTestClient
    @Import(ApiVersionTests.NotAcceptableApiVersionController.class)
    @TestPropertySource(properties = {
            "spring.mvc.apiversion.use.header=API-Version",
            "spring.mvc.apiversion.supported=1,2",
    })
    class ApiVersionTests {

        @Autowired
        private RestTestClient restTestClient;

        /**
         * @see InvalidApiVersionException
         * @see MvcProblemDetailController#invalidApiVersionException()
         */
        @Test
        void invalidApiVersionException() {
            String uri = "http://localhost:" + port + BASE_PATH + "/invalid-api-version-exception";
            EntityExchangeResult<ExtendedProblemDetail> result = restTestClient.get()
                    .uri(uri)
                    .header("API-Version", "3")
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
            assertThat(extendedProblemDetail.getType()).isNull();
            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid API version: '3.0.0'.");
            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create("/mvc-extended-problem-detail/invalid-api-version-exception"));
            assertThat(extendedProblemDetail.getProperties()).isNull();
            assertThat(extendedProblemDetail.getErrors()).isNull();
        }

        /**
         * {@link NotAcceptableApiVersionException}
         */
        @RestController
        static class NotAcceptableApiVersionController {
            @GetMapping(path = "/not-acceptable-api-version", version = "1")
            void notAcceptableApiVersion() {
                log.info("notAcceptableApiVersion");
            }
        }

        /**
         * @see NotAcceptableApiVersionException
         * @see NotAcceptableApiVersionController#notAcceptableApiVersion()
         */
        @Test
        void notAcceptableApiVersionException() {
            String uri = "http://localhost:" + port + "/not-acceptable-api-version";
            EntityExchangeResult<ExtendedProblemDetail> result = restTestClient.get()
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
            assertThat(extendedProblemDetail.getType()).isNull();
            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid API version: '2.0.0'.");
            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create("/not-acceptable-api-version"));
            assertThat(extendedProblemDetail.getProperties()).isNull();
            assertThat(extendedProblemDetail.getErrors()).isNull();
        }

        /**
         * @see MissingApiVersionException
         * @see MvcProblemDetailController#missingApiVersionException()
         */
        @Test
        void missingApiVersionException() {
            String uri = "http://localhost:" + port + BASE_PATH + "/missing-api-version-exception";
            EntityExchangeResult<ExtendedProblemDetail> result = restTestClient.get()
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
            assertThat(extendedProblemDetail.getType()).isNull();
            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
            assertThat(extendedProblemDetail.getDetail()).isEqualTo("API version is required.");
            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create("/mvc-extended-problem-detail/missing-api-version-exception"));
            assertThat(extendedProblemDetail.getProperties()).isNull();
            assertThat(extendedProblemDetail.getErrors()).isNull();
        }
    }

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

        try {
            restClient.get()
                    .uri(BASE_PATH + "/async-request-not-usable-exception")
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Read timed out");
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error("Thread sleep interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}

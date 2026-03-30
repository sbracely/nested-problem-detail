package com.github.sbracely.extended.problem.detail.test.flux.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExtendProblemDetailFluxRandomPortTests {

    @LocalServerPort
    private int port;

    private static final String BASE_PATH = "/mvc-problem-detail";

//    @Nested
//    @AutoConfigureTestRestTemplate
//    @TestPropertySource(properties = "spring.servlet.multipart.max-file-size=1")
//    class FileTests {
//
//        @Autowired
//        private TestRestTemplate restTemplate;
//
//        @Test
//        void maxUploadSizeExceededException() {
//            byte[] largeContent = new byte[2];
//
//            ByteArrayResource resource = new ByteArrayResource(largeContent) {
//                @Override
//                public String getFilename() {
//                    return "large-test-file.txt";
//                }
//            };
//
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("file", resource);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//            String uri = BASE_PATH + "/file-max-size";
//            ResponseEntity<ExtendedProblemDetail> response = restTemplate.postForEntity(
//                    "http://localhost:" + port + uri,
//                    new HttpEntity<>(body, headers),
//                    ExtendedProblemDetail.class
//            );
//            assertThat(response.getStatusCode()).isEqualTo(CONTENT_TOO_LARGE);
//            assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
//            ExtendedProblemDetail extendedProblemDetail = response.getBody();
//            assertThat(extendedProblemDetail).isNotNull();
//            assertThat(extendedProblemDetail.getDetail()).isEqualTo("Maximum upload size exceeded");
//            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
//            assertThat(extendedProblemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
//            assertThat(extendedProblemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
//            assertThat(extendedProblemDetail.getErrors()).isNull();
//        }
//
//    }

//    @Nested
//    @AutoConfigureRestTestClient
//    @Import(ApiVersionTests.ApiVersionTestController.class)
//    @TestPropertySource(properties = {
//            "spring.mvc.apiversion.use.header=API-Version",
//            "spring.mvc.apiversion.supported=1,2",
//    })
//    class ApiVersionTests {
//
//        @Autowired
//        private RestTestClient restTestClient;
//
//        @RestController
//        static class ApiVersionTestController {
//            @GetMapping(path = "/api-version-test", version = "1")
//            void apiVersionTest1() {
//            }
//        }
//
//        @Test
//        void errorResponseExceptionNotAcceptableApiVersionException() {
//            String uri = "http://localhost:" + port + "/api-version-test";
//            EntityExchangeResult<ExtendedProblemDetail> result = restTestClient.get()
//                    .uri(uri)
//                    .header("API-Version", "2")
//                    .exchange()
//                    .expectStatus()
//                    .isEqualTo(BAD_REQUEST)
//                    .expectHeader()
//                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
//                    .expectBody(ExtendedProblemDetail.class)
//                    .returnResult();
//            ExtendedProblemDetail extendedProblemDetail = result.getResponseBody();
//            log.info("extendedProblemDetail: {}", extendedProblemDetail);
//            assertThat(extendedProblemDetail).isNotNull();
//            assertThat(extendedProblemDetail.getDetail()).isEqualTo("Invalid API version: '2.0.0'.");
//            assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create("/api-version-test"));
//            assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
//            assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
//
//        }
//    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void asyncRequestNotUsableException(CapturedOutput output) {
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setReadTimeout(Duration.ofMillis(1));
//
//        RestClient restClient = RestClient.builder()
//                .requestFactory(factory)
//                .baseUrl("http://localhost:" + port)
//                .build();
//
//        try {
//            restClient.get()
//                    .uri(BASE_PATH + "/async-request-not-usable")
//                    .retrieve()
//                    .toBodilessEntity();
//        } catch (Exception e) {
//            assertThat(e).hasMessageContaining("Read timed out");
//        }
//
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            log.error("Thread sleep interrupted", e);
//            Thread.currentThread().interrupt();
//        }
//
//
//        assertThat(output.getOut())
//                .contains("handleAsyncRequestNotUsableException");
    }
}

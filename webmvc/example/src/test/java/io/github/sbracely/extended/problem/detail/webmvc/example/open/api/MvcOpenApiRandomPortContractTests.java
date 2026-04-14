package io.github.sbracely.extended.problem.detail.webmvc.example.open.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Strict OpenAPI contract tests for the <b>random-port</b> configuration scenario (WebMVC).
 * <p>
 * The {@code asyncRequestNotUsableException} operation normally returns an SSE stream to the
 * client. This test verifies the client-visible contract end-to-end by:
 * <ul>
 *     <li>Fetching the operation from the live {@code /v3/api-docs} document.</li>
 *     <li>The documented status code is {@code 200}.</li>
 *     <li>The documented content type is {@code text/event-stream}.</li>
 *     <li>The documented stream example contains the first emitted events.</li>
 *     <li>A real HTTP request returns {@code 200 text/event-stream} and contains
 *         {@code data:event 0}, {@code data:event 1}, and {@code data:event 2}.</li>
 *     <li>The operation carries the {@code x-scenario: "random-port"} extension.</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MvcOpenApiRandomPortContractTests {

    private static final String SCENARIO = "random-port";
    private static final String BASE = "/mvc-extended-problem-detail";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void asyncRequestNotUsableExceptionDocumentationContract() throws Exception {
        String apiDocsBody = testRestTemplate.getForObject("http://localhost:" + port + "/v3/api-docs", String.class);
        JsonNode apiDocs = objectMapper.readTree(apiDocsBody);

        JsonNode operation = apiDocs.path("paths")
                .path(BASE + "/async-request-not-usable-exception")
                .path("get");
        assertThat(operation.isMissingNode())
                .as("asyncRequestNotUsableException operation should be present in API docs")
                .isFalse();

        int docStatus = MvcOpenApiContractTestSupport.extractDocumentedStatus(
                apiDocs, BASE + "/async-request-not-usable-exception", "get");
        assertThat(docStatus)
                .as("documented status for asyncRequestNotUsableException should be 200")
                .isEqualTo(200);

        String xScenario = operation.path("x-scenario").asText(null);
        assertThat(xScenario)
                .as("x-scenario extension on asyncRequestNotUsableException")
                .isEqualTo(SCENARIO);

        JsonNode streamContent = operation.path("responses").path("200")
                .path("content").path("text/event-stream");
        assertThat(streamContent.isMissingNode())
                .as("documented SSE content for asyncRequestNotUsableException should be present")
                .isFalse();
        assertThat(streamContent.path("schema").path("type").asText())
                .as("documented schema type for asyncRequestNotUsableException")
                .isEqualTo("string");
        assertThat(streamContent.path("example").asText())
                .as("documented SSE example for asyncRequestNotUsableException")
                .contains("data:event 0")
                .contains("data:event 1")
                .contains("data:event 2");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.TEXT_EVENT_STREAM));
        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://localhost:" + port + BASE + "/async-request-not-usable-exception",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.TEXT_EVENT_STREAM)).isTrue();
        assertThat(response.getBody())
                .isNotNull()
                .contains("data:event 0")
                .contains("data:event 1")
                .contains("data:event 2");
    }
}

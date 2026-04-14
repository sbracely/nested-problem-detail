package io.github.sbracely.extended.problem.detail.webmvc.example.open.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Strict OpenAPI contract tests for the <b>api-version</b> configuration scenario (WebMVC).
 * <p>
 * Starts a real HTTP server with API version negotiation properties and verifies that
 * {@code invalidApiVersionException} and {@code missingApiVersionException} match their
 * documented OpenAPI examples.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.mvc.apiversion.use.header=API-Version",
        "spring.mvc.apiversion.supported=1,2",
})
class MvcOpenApiApiVersionContractTests {

    private static final String BASE = "/mvc-extended-problem-detail";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void invalidApiVersionExceptionContractMatches() throws Exception {
        JsonNode apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(
                mockMvcTester, "API-Version", "1");
        assertThat(apiDocs.path("paths").path(BASE + "/invalid-api-version-exception").isMissingNode())
                .as("SpringDoc does not expose invalidApiVersionException in Boot 3 live docs")
                .isTrue();

        String uri = "http://localhost:" + port + BASE + "/invalid-api-version-exception";
        HttpHeaders headers = new HttpHeaders();
        headers.set("API-Version", "3");
        ResponseEntity<ExtendedProblemDetail> result = testRestTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ExtendedProblemDetail.class);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(result.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    void missingApiVersionExceptionContractMatches() throws Exception {
        JsonNode apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(
                mockMvcTester, "API-Version", "1");
        assertThat(apiDocs.path("paths").path(BASE + "/missing-api-version-exception").isMissingNode())
                .as("SpringDoc does not expose missingApiVersionException in Boot 3 live docs")
                .isTrue();

        String uri = "http://localhost:" + port + BASE + "/missing-api-version-exception";
        ResponseEntity<ExtendedProblemDetail> result = testRestTemplate.getForEntity(
                uri, ExtendedProblemDetail.class);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(result.getBody().getStatus()).isEqualTo(404);
    }
}

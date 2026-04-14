package io.github.sbracely.extended.problem.detail.webmvc.example.open.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Strict OpenAPI contract tests for the <b>api-version</b> configuration scenario (WebMVC).
 * <p>
 * Starts a real HTTP server with API version negotiation properties and verifies that
 * {@code invalidApiVersionException} and {@code missingApiVersionException} match their
 * documented OpenAPI examples.
 */
@AutoConfigureRestTestClient
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
    private RestTestClient restTestClient;

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void invalidApiVersionExceptionContractMatches() throws Exception {
        JsonNode apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(
                mockMvcTester, "API-Version", "1");
        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, BASE + "/invalid-api-version-exception", "get");
        assertThat(docExample)
                .as("documented example for invalidApiVersionException should be present").isNotNull();

        String uri = "http://localhost:" + port + BASE + "/invalid-api-version-exception";
        EntityExchangeResult<ExtendedProblemDetail> result = restTestClient.get()
                .uri(uri)
                .header("API-Version", "3")
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult();

        MvcOpenApiContractTestSupport.assertContractMatches(result.getResponseBody(), docExample);
    }

    @Test
    void missingApiVersionExceptionContractMatches() throws Exception {
        JsonNode apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(
                mockMvcTester, "API-Version", "1");
        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, BASE + "/missing-api-version-exception", "get");
        assertThat(docExample)
                .as("documented example for missingApiVersionException should be present").isNotNull();

        String uri = "http://localhost:" + port + BASE + "/missing-api-version-exception";
        EntityExchangeResult<ExtendedProblemDetail> result = restTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult();

        MvcOpenApiContractTestSupport.assertContractMatches(result.getResponseBody(), docExample);
    }
}

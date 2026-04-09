package io.github.sbracely.extended.problem.detail.webmvc.example.contract;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Strict OpenAPI contract tests for the <b>random-port</b> configuration scenario (WebMVC).
 * <p>
 * The {@code asyncRequestNotUsableException} operation requires a real HTTP connection that
 * times out before the response body can be read (the server never writes the error back to
 * the client because the async context is no longer usable). Full request/response contract
 * verification is therefore not possible via a standard HTTP client; this test instead
 * confirms the <em>documentation contract</em>:
 * <ul>
 *     <li>The operation is present in {@code /v3/api-docs}.</li>
 *     <li>The documented status code is {@code 500}.</li>
 *     <li>A response example exists and contains the expected {@code title}.</li>
 *     <li>The operation carries the {@code x-scenario: "random-port"} extension.</li>
 * </ul>
 * The behavioural coverage for this operation lives in
 * {@code MvcControllerRandomPortTests#asyncRequestNotUsableException()}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MvcOpenApiRandomPortContractTests {

    private static final String SCENARIO = "random-port";
    private static final String BASE = "/mvc-extended-problem-detail";

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void asyncRequestNotUsableExceptionDocumentationContract() throws Exception {
        JsonNode apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(mockMvcTester);

        // Verify documented status code and scenario metadata
        JsonNode operation = apiDocs.path("paths")
                .path(BASE + "/async-request-not-usable-exception")
                .path("get");
        assertThat(operation.isMissingNode())
                .as("asyncRequestNotUsableException operation should be present in API docs")
                .isFalse();

        int docStatus = MvcOpenApiContractTestSupport.extractDocumentedStatus(
                apiDocs, BASE + "/async-request-not-usable-exception", "get");
        assertThat(docStatus)
                .as("documented status for asyncRequestNotUsableException should be 500")
                .isEqualTo(500);

        String xScenario = operation.path("x-scenario").asText(null);
        assertThat(xScenario)
                .as("x-scenario extension on asyncRequestNotUsableException")
                .isEqualTo(SCENARIO);

        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, BASE + "/async-request-not-usable-exception", "get");
        assertThat(docExample)
                .as("documented example for asyncRequestNotUsableException should be present")
                .isNotNull();

        assertThat(docExample.path("title").asText(null))
                .as("documented title for asyncRequestNotUsableException")
                .isEqualTo("Internal Server Error");

        assertThat(docExample.path("status").asInt(-1))
                .as("documented status in example for asyncRequestNotUsableException")
                .isEqualTo(500);
    }
}

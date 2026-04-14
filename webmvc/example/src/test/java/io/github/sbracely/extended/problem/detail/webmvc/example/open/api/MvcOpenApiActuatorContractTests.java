package io.github.sbracely.extended.problem.detail.webmvc.example.open.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

/**
 * Strict OpenAPI contract tests for the <b>actuator-endpoint</b> configuration scenario (WebMVC).
 * <p>
 * This scenario requires {@code management.endpoints.web.exposure.include=demo}; otherwise the
 * example actuator endpoint is not exposed and the invalid-endpoint request cannot be triggered.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "management.endpoints.web.exposure.include=demo")
class MvcOpenApiActuatorContractTests {

    private static final String SCENARIO = "actuator-endpoint";

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void invalidEndpointBadRequestExceptionContractMatches() throws Exception {
        JsonNode apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(mockMvcTester);
        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, "/actuator/demo/{name}", "get");
        assertThat(docExample)
                .as("documented example for invalidEndpointBadRequestException should be present").isNotNull();

        MvcTestResult result = mockMvcTester.get().uri("/actuator/demo/name").exchange();
        assertThat(result)
                .hasStatus(400)
                .hasContentType(APPLICATION_PROBLEM_JSON);

        ExtendedProblemDetail actual = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        MvcOpenApiContractTestSupport.assertContractMatches(actual, docExample);
    }

    @Test
    void allActuatorOperationsCovered() throws Exception {
        JsonNode apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(mockMvcTester);
        MvcOpenApiContractTestSupport.assertAllScenarioOperationsCovered(
                apiDocs, SCENARIO, MvcOperationFixtures.all());
    }
}

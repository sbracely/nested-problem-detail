package io.github.sbracely.extended.problem.detail.webflux.example.open.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import io.github.sbracely.extended.problem.detail.webflux.example.open.api.FluxOperationFixtures.FluxOperationFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Strict OpenAPI contract tests for the <b>default</b> configuration scenario (WebFlux).
 * <p>
 * For every documented operation that carries {@code x-scenario: "default"} this test:
 * <ol>
 *     <li>Executes the exact trigger request defined by {@link FluxOperationFixtures}.</li>
 *     <li>Reads {@code /v3/api-docs} and looks up the documented response example for the
 *         matching path + method.</li>
 *     <li>Asserts that the runtime HTTP status, media type, and normalized
 *         {@link ExtendedProblemDetail} body match the documented example.</li>
 * </ol>
 * <p>
 * Operations that require non-default Spring properties are covered by
 * {@code FluxOpenApiApiVersionContractTests}.
 */
@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT1M")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FluxOpenApiDefaultContractTests {

    private static final String SCENARIO = "default";

    @Autowired
    private WebTestClient webTestClient;

    private JsonNode apiDocs;
    private Map<String, String[]> operationPaths;

    @BeforeAll
    void fetchApiDocs() throws Exception {
        apiDocs = FluxOpenApiContractTestSupport.fetchApiDocs(webTestClient);
        operationPaths = FluxOpenApiContractTestSupport.operationsByScenario(apiDocs, SCENARIO);
    }

    Collection<String> defaultFixtures() {
        return FluxOperationFixtures.all().entrySet().stream()
                .filter(e -> SCENARIO.equals(e.getValue().scenario()) && e.getValue().requestBuilder() != null)
                .map(Map.Entry::getKey)
                .toList();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("defaultFixtures")
    void contractMatches(String operationId) {
        FluxOperationFixture fixture = FluxOperationFixtures.all().get(operationId);
        assertThat(fixture).as("fixture for %s", operationId).isNotNull();
        assertThat(fixture.requestBuilder()).as("requestBuilder for %s", operationId).isNotNull();

        // execute trigger
        WebTestClient.ResponseSpec responseSpec = fixture.requestBuilder().apply(webTestClient);
        ExtendedProblemDetail actual = responseSpec
                .expectStatus().value(code ->
                        assertThat(code).as("HTTP status for %s", operationId).isEqualTo(fixture.expectedStatus()))
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult()
                .getResponseBody();

        // look up documented example from the API docs
        String[] pathMethod = operationPaths.get(operationId);
        assertThat(pathMethod)
                .as("operationId '%s' should be present in API docs under scenario '%s'", operationId, SCENARIO)
                .isNotNull();
        JsonNode docExample = FluxOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, pathMethod[0], pathMethod[1]);
        assertThat(docExample)
                .as("documented example for operation '%s' at %s %s", operationId, pathMethod[1], pathMethod[0])
                .isNotNull();

        // compare runtime vs documented
        FluxOpenApiContractTestSupport.assertContractMatches(actual, docExample);
    }

    /**
     * Ensures that every operation documented under the {@code "default"} scenario in the
     * API docs has a matching fixture in {@link FluxOperationFixtures}.
     */
    @Test
    void allDefaultOperationsCovered() {
        FluxOpenApiContractTestSupport.assertAllScenarioOperationsCovered(
                apiDocs, SCENARIO, FluxOperationFixtures.all());
    }
}

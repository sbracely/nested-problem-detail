package io.github.sbracely.extended.problem.detail.webmvc.example.open.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import io.github.sbracely.extended.problem.detail.webmvc.example.open.api.MvcOperationFixtures.MvcOperationFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncListener;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Strict OpenAPI contract tests for the <b>default</b> configuration scenario (WebMVC).
 * <p>
 * For every documented operation that carries {@code x-scenario: "default"} this test:
 * <ol>
 *     <li>Executes the exact trigger request defined by {@link MvcOperationFixtures}.</li>
 *     <li>Reads {@code /v3/api-docs} and looks up the documented response example for the
 *         matching path + method.</li>
 *     <li>Asserts that the runtime HTTP status, media type, and normalized
 *         {@link ExtendedProblemDetail} body match the documented example.</li>
 * </ol>
 * <p>
 * Operations that require non-default Spring properties are covered by their own scenario
 * classes ({@code MvcOpenApiRandomPortContractTests}, {@code MvcOpenApiMultipartContractTests},
 * {@code MvcOpenApiNoHandlerFoundContractTests}, and {@code MvcOpenApiActuatorContractTests}).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MvcOpenApiDefaultContractTests {

    private static final String SCENARIO = "default";
    private static final String BASE = "/mvc-extended-problem-detail";

    @Autowired
    private MockMvcTester mockMvcTester;

    private JsonNode apiDocs;
    private Map<String, String[]> operationPaths;

    @BeforeAll
    void fetchApiDocs() throws Exception {
        apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(mockMvcTester);
        operationPaths = MvcOpenApiContractTestSupport.operationsByScenario(apiDocs, SCENARIO);
    }

    Collection<String> defaultFixtures() {
        return MvcOperationFixtures.all().entrySet().stream()
                .filter(e -> SCENARIO.equals(e.getValue().scenario()) && e.getValue().requestBuilder() != null)
                .map(Map.Entry::getKey)
                .toList();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("defaultFixtures")
    void contractMatches(String operationId) throws Exception {
        MvcOperationFixture fixture = MvcOperationFixtures.all().get(operationId);
        assertThat(fixture).as("fixture for %s", operationId).isNotNull();
        assertThat(fixture.requestBuilder()).as("requestBuilder for %s", operationId).isNotNull();

        // execute trigger
        MvcTestResult result = fixture.requestBuilder().apply(mockMvcTester);
        assertThat(result)
                .as("HTTP status for %s", operationId)
                .hasStatus(fixture.expectedStatus())
                .hasContentType("httpMessageNotWritableException".equals(operationId)
                        ? MediaType.APPLICATION_JSON
                        : MediaType.APPLICATION_PROBLEM_JSON);

        // deserialize runtime body
        ExtendedProblemDetail actual = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();

        // look up documented example from the API docs
        String[] pathMethod = operationPaths.get(operationId);
        assertThat(pathMethod)
                .as("operationId '%s' should be present in API docs under scenario '%s'", operationId, SCENARIO)
                .isNotNull();
        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, pathMethod[0], pathMethod[1]);
        assertThat(docExample)
                .as("documented example for operation '%s' at %s %s", operationId, pathMethod[1], pathMethod[0])
                .isNotNull();

        // compare runtime vs documented
        MvcOpenApiContractTestSupport.assertContractMatches(actual, docExample);
    }

    /**
     * Contract test for {@code asyncRequestTimeoutException}.
     * <p>
     * This operation requires a two-step MockMvc async dispatch because the timeout is
     * triggered via an {@link AsyncListener} callback rather than by a direct response.
     */
    @Test
    void asyncRequestTimeoutExceptionContractMatches() throws Exception {
        String uri = BASE + "/async-request-timeout-exception";

        // Step 1: start the async request
        MvcTestResult asyncResult = mockMvcTester.get().uri(uri).asyncExchange();
        assertThat(asyncResult.getRequest().isAsyncStarted()).isTrue();

        // Step 2: manually trigger the timeout listener
        AsyncContext asyncContext = asyncResult.getRequest().getAsyncContext();
        assertThat(asyncContext).isNotNull();
        AsyncListener listener = ((MockAsyncContext) asyncContext).getListeners().get(0);
        listener.onTimeout(null);

        // Step 3: dispatch async result
        MvcTestResult result = mockMvcTester.perform(
                MockMvcRequestBuilders.asyncDispatch(asyncResult.getMvcResult()));
        assertThat(result)
                .hasStatus(503)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);

        ExtendedProblemDetail actual = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();

        // Fetch documented example
        String[] pathMethod = operationPaths.get("asyncRequestTimeoutException");
        assertThat(pathMethod).as("asyncRequestTimeoutException should be in API docs").isNotNull();
        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, pathMethod[0], pathMethod[1]);
        assertThat(docExample).as("documented example for asyncRequestTimeoutException").isNotNull();

        MvcOpenApiContractTestSupport.assertContractMatches(actual, docExample);
    }

    /**
     * Ensures that every operation documented under the {@code "default"} scenario in the
     * API docs has a matching fixture in {@link MvcOperationFixtures}.
     */
    @Test
    void allDefaultOperationsCovered() {
        MvcOpenApiContractTestSupport.assertAllScenarioOperationsCovered(
                apiDocs, SCENARIO, MvcOperationFixtures.all());
    }
}

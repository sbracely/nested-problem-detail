package io.github.sbracely.extended.problem.detail.webmvc.example.open.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ProblemDetail;
import io.github.sbracely.extended.problem.detail.webmvc.example.open.api.MvcOperationFixtures.MvcOperationFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
 * Strict OpenAPI contract tests for the default, controller-backed MVC operations.
 * <p>
 * For every default-scenario fixture this test:
 * <ol>
 *     <li>Executes the exact trigger request defined by {@link MvcOperationFixtures}.</li>
 *     <li>Reads {@code /v3/api-docs} and looks up the documented response example for the
 *         matching path + method.</li>
 *     <li>Asserts that the runtime HTTP status, media type, and normalized
 *         {@link ProblemDetail} body match the documented example.</li>
 * </ol>
 * <p>
 * Operations that require non-default Spring properties are covered by their own scenario
 * classes ({@code MvcOpenApiRandomPortContractTests}, {@code MvcOpenApiMultipartContractTests},
 * {@code MvcOpenApiApiVersionContractTests}, {@code MvcOpenApiNoHandlerFoundContractTests}, and
 * {@code MvcOpenApiActuatorContractTests}).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MvcOpenApiDefaultContractTests {

    private static final String BASE = "/mvc-extended-problem-detail";

    @Autowired
    private MockMvcTester mockMvcTester;

    private JsonNode apiDocs;

    @BeforeAll
    void fetchApiDocs() throws Exception {
        apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(mockMvcTester);
    }

    Collection<String> defaultFixtures() {
        return MvcOperationFixtures.all().entrySet().stream()
                .filter(e -> "default".equals(e.getValue().scenario()) && e.getValue().requestBuilder() != null)
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
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);

        // deserialize runtime body
        ProblemDetail actual = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();

        // look up documented example from the API docs
        String docPath = fixture.docPath();
        String docMethod = fixture.docMethod();
        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, docPath, docMethod);
        assertThat(docExample)
                .as("documented example for operation '%s' at %s %s", operationId, docMethod, docPath)
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

        ProblemDetail actual = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();

        // Fetch documented example
        MvcOperationFixture fixture = MvcOperationFixtures.all().get("asyncRequestTimeoutException");
        assertThat(fixture).as("fixture for asyncRequestTimeoutException").isNotNull();
        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, fixture.docPath(), fixture.docMethod());
        assertThat(docExample).as("documented example for asyncRequestTimeoutException").isNotNull();

        MvcOpenApiContractTestSupport.assertContractMatches(actual, docExample);
    }

    /**
     * Ensures that every default-scenario fixture still points at a documented OpenAPI example.
     */
    @Test
    void allDefaultFixturesDocumented() {
        MvcOperationFixtures.all().forEach((operationId, fixture) -> {
            if (!"default".equals(fixture.scenario())) {
                return;
            }
            JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                    apiDocs, fixture.docPath(), fixture.docMethod());
            assertThat(docExample)
                    .as("documented example for default operation '%s' at %s %s",
                            operationId, fixture.docMethod(), fixture.docPath())
                    .isNotNull();
        });
    }
}

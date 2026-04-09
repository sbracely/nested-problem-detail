package io.github.sbracely.extended.problem.detail.webmvc.example.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Shared utilities for OpenAPI strict contract tests (WebMVC).
 * <p>
 * Provides helpers to:
 * <ul>
 *     <li>Fetch and parse {@code /v3/api-docs} from a running MockMvc context.</li>
 *     <li>Extract the documented response example for a specific operation.</li>
 *     <li>Assert that a runtime {@link ExtendedProblemDetail} matches the documented example.</li>
 * </ul>
 */
public final class MvcOpenApiContractTestSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MvcOpenApiContractTestSupport() {
    }

    /**
     * Fetches {@code /v3/api-docs} from the given {@link MockMvcTester} and returns the parsed
     * {@link JsonNode}.
     */
    public static JsonNode fetchApiDocs(MockMvcTester mockMvcTester) throws Exception {
        return fetchApiDocs(mockMvcTester, new String[0]);
    }

    /**
     * Fetches {@code /v3/api-docs} from the given {@link MockMvcTester} with additional
     * request headers and returns the parsed {@link JsonNode}.
     *
     * @param headers alternating name/value pairs, e.g. {@code "API-Version", "1"}
     */
    public static JsonNode fetchApiDocs(MockMvcTester mockMvcTester, String... headers) throws Exception {
        var request = mockMvcTester.get().uri("/v3/api-docs");
        for (int i = 0; i + 1 < headers.length; i += 2) {
            request = request.header(headers[i], headers[i + 1]);
        }
        MvcTestResult result = request.exchange();
        assertThat(result).hasStatusOk().hasContentType(APPLICATION_JSON);
        String body = result.getResponse().getContentAsString();
        return MAPPER.readTree(body);
    }

    /**
     * Extracts the documented response example value node for the given {@code path} and HTTP
     * {@code method} from the already-parsed API docs. Returns the first example found in the
     * first documented response, or {@code null} when no example is present.
     *
     * @param apiDocs parsed {@code /v3/api-docs} root node
     * @param path    path exactly as it appears in the spec (e.g.
     *                {@code "/mvc-extended-problem-detail/method-not-allowed-exception"})
     * @param method  lowercase HTTP method (e.g. {@code "post"})
     */
    public static JsonNode extractDocumentedExample(JsonNode apiDocs, String path, String method) {
        JsonNode pathNode = apiDocs.path("paths").path(path);
        JsonNode operation = pathNode.path(method.toLowerCase());
        if (operation.isMissingNode()) {
            return null;
        }
        JsonNode responses = operation.path("responses");
        for (JsonNode response : responses) {
            JsonNode examples = response.path("content")
                    .path("application/problem+json")
                    .path("examples");
            for (JsonNode exampleNode : examples) {
                JsonNode value = exampleNode.path("value");
                if (!value.isMissingNode()) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Returns the documented HTTP status code (as an int) for the given {@code path} and
     * {@code method}, or {@code -1} if not found.
     */
    public static int extractDocumentedStatus(JsonNode apiDocs, String path, String method) {
        JsonNode pathNode = apiDocs.path("paths").path(path);
        JsonNode operation = pathNode.path(method.toLowerCase());
        if (operation.isMissingNode()) {
            return -1;
        }
        JsonNode responses = operation.path("responses");
        if (responses.isObject()) {
            String firstKey = responses.fieldNames().next();
            try {
                return Integer.parseInt(firstKey);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Asserts that the runtime {@link ExtendedProblemDetail} matches the documented example.
     * <p>
     * Stable fields compared:
     * <ul>
     *     <li>{@code title} – exact match</li>
     *     <li>{@code status} – exact match</li>
     *     <li>{@code detail} – exact match when the documented example contains a non-null value;
     *         skipped otherwise</li>
     *     <li>{@code errors} – order-insensitive containment when the documented example contains
     *         an {@code errors} array; skipped otherwise</li>
     * </ul>
     * The {@code instance} field is intentionally not compared because example values use
     * concrete paths while path-template operations may produce a different literal value.
     *
     * @param actual      runtime response deserialized from the HTTP response body
     * @param docExample  the {@link JsonNode} obtained from
     *                    {@link #extractDocumentedExample(JsonNode, String, String)}
     */
    public static void assertContractMatches(ExtendedProblemDetail actual, JsonNode docExample) {
        assertThat(actual).isNotNull();
        assertThat(docExample).isNotNull();

        // title
        String docTitle = docExample.path("title").asText(null);
        if (docTitle != null) {
            assertThat(actual.getTitle())
                    .as("title should match documented example")
                    .isEqualTo(docTitle);
        }

        // status
        if (docExample.hasNonNull("status")) {
            assertThat(actual.getStatus())
                    .as("status should match documented example")
                    .isEqualTo(docExample.get("status").asInt());
        }

        // detail – only compare when documented (skip null/missing placeholders)
        if (docExample.hasNonNull("detail")) {
            assertThat(actual.getDetail())
                    .as("detail should match documented example")
                    .isEqualTo(docExample.get("detail").asText());
        }

        // errors – order-insensitive when present in example
        if (docExample.hasNonNull("errors")) {
            List<Error> docErrors = new ArrayList<>();
            for (JsonNode errorNode : docExample.get("errors")) {
                String type = errorNode.path("type").asText(null);
                String target = errorNode.hasNonNull("target") ? errorNode.get("target").asText() : null;
                String message = errorNode.path("message").asText(null);
                docErrors.add(new Error(Error.Type.valueOf(type), target, message));
            }
            assertThat(actual.getErrors())
                    .as("errors should match documented example (order-insensitive)")
                    .containsExactlyInAnyOrderElementsOf(docErrors);
        }
    }

    /**
     * Asserts that every documented operation in the {@code paths} section of the API docs that
     * carries the given {@code scenario} tag has a corresponding entry in the provided
     * {@code fixtureMap}. This ensures no documented operation is silently skipped.
     *
     * @param apiDocs    parsed API docs root node
     * @param scenario   the scenario name (e.g. {@code "default"}, {@code "api-version"})
     * @param fixtureMap map from operationId to fixture – used to confirm coverage
     */
    public static void assertAllScenarioOperationsCovered(JsonNode apiDocs, String scenario,
                                                          Map<String, ?> fixtureMap) {
        List<String> missing = new ArrayList<>();
        apiDocs.path("paths").fields().forEachRemaining(pathEntry ->
                pathEntry.getValue().fields().forEachRemaining(methodEntry -> {
                    JsonNode operation = methodEntry.getValue();
                    String opScenario = operation.path("x-scenario").asText(null);
                    if (scenario.equals(opScenario)) {
                        String operationId = operation.path("operationId").asText(null);
                        if (operationId != null && !fixtureMap.containsKey(operationId)) {
                            missing.add(operationId);
                        }
                    }
                }));
        assertThat(missing)
                .as("All documented operations for scenario '%s' should have a contract fixture", scenario)
                .isEmpty();
    }

    /**
     * Converts an already-parsed API docs {@link JsonNode} into a map of
     * {@code operationId → (path, method)} pairs for all operations belonging to
     * {@code scenario}.
     */
    public static Map<String, String[]> operationsByScenario(JsonNode apiDocs, String scenario) {
        Map<String, String[]> result = new HashMap<>();
        apiDocs.path("paths").fields().forEachRemaining(pathEntry -> {
            String path = pathEntry.getKey();
            pathEntry.getValue().fields().forEachRemaining(methodEntry -> {
                String method = methodEntry.getKey();
                JsonNode operation = methodEntry.getValue();
                String opScenario = operation.path("x-scenario").asText(null);
                if (scenario.equals(opScenario)) {
                    String operationId = operation.path("operationId").asText(null);
                    if (operationId != null) {
                        result.put(operationId, new String[]{path, method});
                    }
                }
            });
        });
        return result;
    }
}

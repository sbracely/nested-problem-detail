package io.github.sbracely.extended.problem.detail.webmvc.example.open.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sbracely.extended.problem.detail.common.response.Error;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Shared utilities for OpenAPI strict contract tests (WebMVC).
 * <p>
 * Provides helpers to:
 * <ul>
 *     <li>Fetch and parse {@code /v3/api-docs} from a running MockMvc context.</li>
 *     <li>Extract the documented response example for a specific operation.</li>
 *     <li>Assert that a runtime {@link ProblemDetail} matches the documented example.</li>
 * </ul>
 */
public final class MvcOpenApiContractTestSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<Error>> ERRORS_TYPE = new TypeReference<>() {
    };
    private static final String MISSING_PARAMETERS_PREFIX = "Missing parameters: ";

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
     * Asserts that the runtime {@link ProblemDetail} matches the documented example.
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
     * @param actual     runtime response deserialized from the HTTP response body
     * @param docExample the {@link JsonNode} obtained from
     *                   {@link #extractDocumentedExample(JsonNode, String, String)}
     */
    public static void assertContractMatches(ProblemDetail actual, JsonNode docExample) {
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
            assertThat(normalizeDetail(actual.getDetail()))
                    .as("detail should match documented example")
                    .isEqualTo(normalizeDetail(docExample.get("detail").asText()));
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
            assertThat(errorsOf(actual))
                    .as("errors should match documented example (order-insensitive)")
                    .containsExactlyInAnyOrderElementsOf(docErrors);
        }
    }

    private static @Nullable String normalizeDetail(@Nullable String detail) {
        if (detail == null) {
            return null;
        }
        if (!detail.startsWith(MISSING_PARAMETERS_PREFIX)) {
            return detail;
        }
        String normalizedParameters = Arrays.stream(detail.substring(MISSING_PARAMETERS_PREFIX.length()).split(","))
                .map(String::trim)
                .filter(parameter -> !parameter.isEmpty())
                .sorted()
                .collect(Collectors.joining(","));
        return MISSING_PARAMETERS_PREFIX + normalizedParameters;
    }

    private static @Nullable List<Error> errorsOf(ProblemDetail problemDetail) {
        if (problemDetail.getProperties() == null || problemDetail.getProperties().get("errors") == null) {
            return null;
        }
        return MAPPER.convertValue(problemDetail.getProperties().get("errors"), ERRORS_TYPE);
    }

}

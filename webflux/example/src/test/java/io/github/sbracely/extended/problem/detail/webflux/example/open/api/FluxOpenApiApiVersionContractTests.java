package io.github.sbracely.extended.problem.detail.webflux.example.open.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.accept.NotAcceptableApiVersionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Strict OpenAPI contract tests for the <b>api-version</b> configuration scenario (WebFlux).
 * <p>
 * Boots a reactive application with API version negotiation enabled and verifies that
 * {@code invalidApiVersionException}, {@code missingApiVersionException}, and
 * {@code notAcceptableApiVersionException} match their documented OpenAPI examples.
 */
@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT1M")
@Import(FluxOpenApiApiVersionContractTests.FluxNotAcceptableApiVersionController.class)
@TestPropertySource(properties = {
        "spring.webflux.apiversion.use.header=API-Version",
        "spring.webflux.apiversion.supported=1,2",
})
class FluxOpenApiApiVersionContractTests {

    private static final String SCENARIO = "api-version";
    private static final String BASE = "/flux-extended-problem-detail";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void invalidApiVersionExceptionContractMatches() throws Exception {
        JsonNode apiDocs = FluxOpenApiContractTestSupport.fetchApiDocs(webTestClient, "API-Version", "1");
        JsonNode docExample = FluxOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, BASE + "/invalid-api-version-exception", "get");
        assertThat(docExample)
                .as("documented example for invalidApiVersionException should be present").isNotNull();

        ExtendedProblemDetail actual = webTestClient.get()
                .uri(BASE + "/invalid-api-version-exception")
                .header("API-Version", "3")
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult()
                .getResponseBody();

        FluxOpenApiContractTestSupport.assertContractMatches(actual, docExample);
    }

    @Test
    void missingApiVersionExceptionContractMatches() throws Exception {
        JsonNode apiDocs = FluxOpenApiContractTestSupport.fetchApiDocs(webTestClient, "API-Version", "1");
        JsonNode docExample = FluxOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, BASE + "/missing-api-version-exception", "get");
        assertThat(docExample)
                .as("documented example for missingApiVersionException should be present").isNotNull();

        ExtendedProblemDetail actual = webTestClient.get()
                .uri(BASE + "/missing-api-version-exception")
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult()
                .getResponseBody();

        FluxOpenApiContractTestSupport.assertContractMatches(actual, docExample);
    }

    @Test
    void notAcceptableApiVersionExceptionContractMatches() throws Exception {
        JsonNode apiDocs = FluxOpenApiContractTestSupport.fetchApiDocs(webTestClient, "API-Version", "1");
        JsonNode docExample = FluxOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, "/not-acceptable-api-version", "get");
        assertThat(docExample)
                .as("documented example for notAcceptableApiVersionException should be present").isNotNull();

        ExtendedProblemDetail actual = webTestClient.get()
                .uri("/not-acceptable-api-version")
                .header("API-Version", "2")
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(ExtendedProblemDetail.class)
                .returnResult()
                .getResponseBody();

        FluxOpenApiContractTestSupport.assertContractMatches(actual, docExample);
    }

    @Test
    void allApiVersionOperationsCovered() throws Exception {
        JsonNode apiDocs = FluxOpenApiContractTestSupport.fetchApiDocs(webTestClient, "API-Version", "1");
        FluxOpenApiContractTestSupport.assertAllScenarioOperationsCovered(
                apiDocs, SCENARIO, FluxOperationFixtures.all());
    }

    /**
     * {@link NotAcceptableApiVersionException}
     * <p>
     * This test-only controller is required because Spring only raises
     * {@code NotAcceptableApiVersionException} when version negotiation is enabled and at least one
     * competing versioned handler exists.
     */
    @RestController
    static class FluxNotAcceptableApiVersionController {
        @GetMapping(path = "/not-acceptable-api-version", version = "1")
        Mono<Void> notAcceptableApiVersion() {
            return Mono.empty();
        }
    }
}

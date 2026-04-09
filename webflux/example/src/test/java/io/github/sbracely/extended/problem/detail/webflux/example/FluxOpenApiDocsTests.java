package io.github.sbracely.extended.problem.detail.webflux.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT1M")
class FluxOpenApiDocsTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldExposeOpenApiJson() {
        String body = webTestClient.get().uri("/v3/api-docs").exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThat(body)
                .contains("\"openapi\":\"3.1.0\"")
                .contains("\"components\":{\"schemas\":")
                .contains("\"Error\":{\"type\":\"object\"")
                .contains("\"ExtendedProblemDetail\":{\"type\":\"object\"")
                .contains("/flux-extended-problem-detail/method-not-allowed-exception")
                .contains("Extended Problem Detail WebFlux Example API")
                .contains("\"application/problem+json\"")
                .contains("\"405\"")
                .contains("\"500\"")
                .contains("\"summary\":\"Validation error\"")
                .contains("\"value\":{\"title\":\"Bad Request\",\"status\":400")
                .contains("\"target\":\"name\"")
                .contains("\"message\":\"Name length must be between 6-10\"")
                .contains("FluxControllerTests.java")
                .doesNotContain("\"responseStatusException\":{\"description\":\"OK\"")
                .doesNotContain("\"notAcceptableStatusException\":{\"description\":\"OK\"");
    }

    @Test
    void shouldExposeOpenApiYaml() {
        String body = webTestClient.get().uri("/v3/api-docs.yaml").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThat(body)
                .contains("openapi: 3.1.0")
                .contains("components:")
                .contains("schemas:")
                .contains("ExtendedProblemDetail:")
                .contains("Error:")
                .contains("/flux-extended-problem-detail/method-not-allowed-exception")
                .contains("Extended Problem Detail WebFlux Example API")
                .contains("application/problem+json:")
                .contains("\"405\":")
                .contains("\"500\":")
                .contains("summary: Validation error")
                .contains("status: 400")
                .contains("detail: Invalid request content.")
                .contains("target: name")
                .contains("message: Name length must be between 6-10")
                .contains("FluxControllerTests.java")
                .doesNotContain("\"200\":");
    }

    @Test
    void shouldExposeSwaggerUi() {
        String body = webTestClient.get().uri("/swagger-ui/index.html").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThat(body)
                .contains("Swagger UI")
                .contains("swagger-ui-bundle.js")
                .contains("swagger-initializer.js");

        String config = webTestClient.get().uri("/v3/api-docs/swagger-config").exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThat(config).contains("/v3/api-docs");
    }
}

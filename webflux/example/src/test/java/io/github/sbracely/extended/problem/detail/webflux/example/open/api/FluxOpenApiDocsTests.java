package io.github.sbracely.extended.problem.detail.webflux.example.open.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT1M")
class FluxOpenApiDocsTests {

    private static final Path OPENAPI_JSON = Path.of("docs", "openapi.json");
    private static final Path OPENAPI_YAML = Path.of("docs", "openapi.yaml");

    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                .contains("\"ExtendedProblemDetail\":{\"type\":\"object\"")
                .contains("/flux-extended-problem-detail/method-not-allowed-exception")
                .contains("Extended Problem Detail Boot 4 WebFlux Example API")
                .contains("Example Spring Boot 4 WebFlux endpoints that demonstrate Extended Problem Detail responses.")
                .contains("\"application/problem+json\"")
                .contains("\"405\"")
                .contains("\"500\"")
                .contains("\"summary\":\"Validation error\"")
                .contains("\"value\":{\"title\":\"Bad Request\",\"status\":400")
                .contains("\"target\":\"name\"")
                .contains("\"message\":\"Name length must be between 6-10\"")
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
                .contains("/flux-extended-problem-detail/method-not-allowed-exception")
                .contains("Extended Problem Detail Boot 4 WebFlux Example API")
                .contains("Example Spring Boot 4 WebFlux endpoints")
                .contains("Detail responses.")
                .contains("application/problem+json:")
                .contains("\"405\":")
                .contains("\"500\":")
                .contains("summary: Validation error")
                .contains("status: 400")
                .contains("detail: Invalid request content.")
                .contains("target: name")
                .contains("message: Name length must be between 6-10")
                .doesNotContain("\"200\":");
    }

    @Test
    void shouldMatchStaticOpenApiJsonExport() throws Exception {
        assertThat(OPENAPI_JSON).exists();
        String liveJson = webTestClient.get().uri("/v3/api-docs").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        String exportedJson = Files.readString(OPENAPI_JSON, StandardCharsets.UTF_8);
        assertThat(objectMapper.readTree(exportedJson)).isEqualTo(objectMapper.readTree(liveJson));
    }

    @Test
    void shouldMatchStaticOpenApiYamlExport() throws Exception {
        assertThat(OPENAPI_YAML).exists();
        String liveYaml = webTestClient.get().uri("/v3/api-docs.yaml").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        String exportedYaml = Files.readString(OPENAPI_YAML, StandardCharsets.UTF_8);
        assertThat(normalizeLineEndings(exportedYaml)).isEqualTo(normalizeLineEndings(liveYaml));
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

    private static String normalizeLineEndings(String content) {
        return content.replace("\r\n", "\n").trim();
    }
}

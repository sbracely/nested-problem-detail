package io.github.sbracely.extended.problem.detail.webmvc.example.open.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc
class MvcOpenApiDocsTests {

    private static final Path OPENAPI_JSON = Path.of("docs", "openapi.json");
    private static final Path OPENAPI_YAML = Path.of("docs", "openapi.yaml");

    @Autowired
    private MockMvcTester mockMvcTester;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldExposeOpenApiJson() throws Exception {
        MvcTestResult result = mockMvcTester.get().uri("/v3/api-docs").exchange();
        assertThat(result)
                .hasStatusOk()
                .hasContentType(APPLICATION_JSON);
        String body = result.getResponse().getContentAsString();
        assertThat(body)
                .contains("\"openapi\":\"3.1.0\"")
                .contains("\"components\":{\"schemas\":")
                .contains("\"Error\":{\"type\":\"object\"")
                .contains("\"ExtendedProblemDetail\":{\"type\":\"object\"")
                .contains("/mvc-extended-problem-detail/http-request-method-not-supported-exception")
                .contains("Extended Problem Detail Boot 3 WebMVC Example API")
                .contains("/mvc-extended-problem-detail/async-request-not-usable-exception")
                .contains("Example Spring Boot 3 WebMVC endpoints that demonstrate Extended Problem Detail responses.")
                .contains("\"application/problem+json\"")
                .contains("\"text/event-stream\"")
                .contains("\"405\"")
                .contains("\"200\"")
                .contains("\"summary\":\"Validation error\"")
                .contains("\"value\":{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400")
                .contains("\"target\":\"name\"")
                .contains("\"message\":\"Name length must be between 6-10\"")
                .contains("MvcControllerTests.java")
                .doesNotContain("\"responseStatusException\":{\"description\":\"OK\"")
                .doesNotContain("\"notAcceptableStatusException\":{\"description\":\"OK\"");
    }

    @Test
    void shouldExposeOpenApiYaml() throws Exception {
        MvcTestResult result = mockMvcTester.get().uri("/v3/api-docs.yaml").exchange();
        assertThat(result).hasStatusOk();
        String body = result.getResponse().getContentAsString();
        assertThat(body)
                .contains("openapi: 3.1.0")
                .contains("components:")
                .contains("schemas:")
                .contains("ExtendedProblemDetail:")
                .contains("Error:")
                .contains("/mvc-extended-problem-detail/http-request-method-not-supported-exception")
                .contains("Extended Problem Detail Boot 3 WebMVC Example API")
                .contains("/mvc-extended-problem-detail/async-request-not-usable-exception")
                .contains("Example Spring Boot 3 WebMVC endpoints")
                .contains("Detail responses.")
                .contains("application/problem+json:")
                .contains("text/event-stream:")
                .contains("\"200\":")
                .contains("\"405\":")
                .contains("\"500\":")
                .contains("summary: Validation error")
                .contains("status: 400")
                .contains("detail: Invalid request content.")
                .contains("target: name")
                .contains("message: Name length must be between 6-10")
                .contains("MvcControllerTests.java");
    }

    @Test
    void shouldMatchStaticOpenApiJsonExport() throws Exception {
        assertThat(OPENAPI_JSON).exists();
        String liveJson = mockMvcTester.get().uri("/v3/api-docs").exchange()
                .getResponse().getContentAsString();
        String exportedJson = Files.readString(OPENAPI_JSON, StandardCharsets.UTF_8);
        assertThat(objectMapper.readTree(exportedJson)).isEqualTo(objectMapper.readTree(liveJson));
    }

    @Test
    void shouldMatchStaticOpenApiYamlExport() throws Exception {
        assertThat(OPENAPI_YAML).exists();
        String liveYaml = mockMvcTester.get().uri("/v3/api-docs.yaml").exchange()
                .getResponse().getContentAsString();
        String exportedYaml = Files.readString(OPENAPI_YAML, StandardCharsets.UTF_8);
        assertThat(normalizeLineEndings(exportedYaml)).isEqualTo(normalizeLineEndings(liveYaml));
    }

    @Test
    void shouldExposeSwaggerUi() throws Exception {
        MvcTestResult result = mockMvcTester.get().uri("/swagger-ui/index.html").exchange();
        assertThat(result).hasStatusOk();
        String body = result.getResponse().getContentAsString();
        assertThat(body)
                .contains("Swagger UI")
                .contains("swagger-ui-bundle.js")
                .contains("swagger-initializer.js");

        MvcTestResult configResult = mockMvcTester.get().uri("/v3/api-docs/swagger-config").exchange();
        assertThat(configResult)
                .hasStatusOk()
                .hasContentType(APPLICATION_JSON);
        assertThat(configResult.getResponse().getContentAsString())
                .contains("/v3/api-docs");
    }

    private static String normalizeLineEndings(String content) {
        return content.replace("\r\n", "\n").trim();
    }
}

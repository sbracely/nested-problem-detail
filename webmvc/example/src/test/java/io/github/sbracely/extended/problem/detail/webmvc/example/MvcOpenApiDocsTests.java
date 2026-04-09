package io.github.sbracely.extended.problem.detail.webmvc.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc
class MvcOpenApiDocsTests {

    @Autowired
    private MockMvcTester mockMvcTester;

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
                .contains("Extended Problem Detail WebMVC Example API")
                .contains("\"application/problem+json\"")
                .contains("\"405\"")
                .contains("\"500\"")
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
                .contains("Extended Problem Detail WebMVC Example API")
                .contains("application/problem+json:")
                .contains("\"405\":")
                .contains("\"500\":")
                .contains("summary: Validation error")
                .contains("status: 400")
                .contains("detail: Invalid request content.")
                .contains("target: name")
                .contains("message: Name length must be between 6-10")
                .contains("MvcControllerTests.java")
                .doesNotContain("\"200\":");
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
}

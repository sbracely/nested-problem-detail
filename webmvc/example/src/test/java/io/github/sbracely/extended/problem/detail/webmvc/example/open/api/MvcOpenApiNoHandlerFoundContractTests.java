package io.github.sbracely.extended.problem.detail.webmvc.example.open.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ProblemDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

/**
 * Strict OpenAPI contract tests for the <b>no-handler-found</b> configuration scenario (WebMVC).
 * <p>
 * This scenario requires {@code spring.web.resources.add-mappings=false}; otherwise Spring handles
 * the same request as a static-resource lookup and raises {@code NoResourceFoundException} instead.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.web.resources.add-mappings=false")
class MvcOpenApiNoHandlerFoundContractTests {
    private static final String BASE = "/mvc-extended-problem-detail";

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void noHandlerFoundExceptionContractMatches() throws Exception {
        JsonNode apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(mockMvcTester);
        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, BASE + "/no-handler-found-exception", "get");
        assertThat(docExample)
                .as("documented example for noHandlerFoundException should be present").isNotNull();

        MvcTestResult result = mockMvcTester.get().uri(BASE + "/no-handler-found-exception").exchange();
        assertThat(result)
                .hasStatus(404)
                .hasContentType(APPLICATION_PROBLEM_JSON);

        ProblemDetail actual = assertThat(result).bodyJson()
                .convertTo(ProblemDetail.class).isNotNull().actual();
        MvcOpenApiContractTestSupport.assertContractMatches(actual, docExample);
    }

    @Test
    void noHandlerFoundFixtureRemainsDocumented() throws Exception {
        JsonNode apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(mockMvcTester);
        MvcOperationFixtures.MvcOperationFixture fixture = MvcOperationFixtures.all().get("noHandlerFoundException");
        assertThat(fixture).as("fixture for noHandlerFoundException").isNotNull();
        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, fixture.docPath(), fixture.docMethod());
        assertThat(docExample)
                .as("documented example for noHandlerFoundException at %s %s",
                        fixture.docMethod(), fixture.docPath())
                .isNotNull();
    }
}

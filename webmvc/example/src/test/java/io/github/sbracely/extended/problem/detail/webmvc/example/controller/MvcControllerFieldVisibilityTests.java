package io.github.sbracely.extended.problem.detail.webmvc.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ALLOW;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "extended.problem-detail.field.hide[0]=status",
        "extended.problem-detail.field.hide[1]=detail",
        "extended.problem-detail.field.hide[2]=instance",
        "extended.problem-detail.field.hide[3]=errors"
})
class MvcControllerFieldVisibilityTests {

    private static final String BASE_PATH = "/mvc-extended-problem-detail";

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void shouldHideConfiguredStandardFields() throws Exception {
        String uri = BASE_PATH + "/http-request-method-not-supported-exception";
        MvcTestResult result = mockMvcTester.post().uri(uri).exchange();

        assertThat(result)
                .hasStatus(METHOD_NOT_ALLOWED)
                .hasContentType(APPLICATION_PROBLEM_JSON)
                .hasHeader(ALLOW, GET.name());
        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("\"title\":\"Method Not Allowed\"");
        assertThat(body).doesNotContain("\"status\":");
        assertThat(body).doesNotContain("\"detail\":");
        assertThat(body).doesNotContain("\"instance\":");
        assertThat(body).doesNotContain("\"errors\":");
    }
}

package io.github.sbracely.extended.problem.detail.webmvc.example.field.hide;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "extended.problem-detail.field.hide[0]=errors.target")
class MvcErrorFieldVisibilityTests {

    private static final String BASE_PATH = "/mvc-extended-problem-detail";

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void shouldHideConfiguredNestedErrorFields() throws Exception {
        String uri = BASE_PATH + "/method-argument-not-valid-exception";
        MvcTestResult result = mockMvcTester.post().uri(uri).contentType(APPLICATION_JSON).content("""
                                {
                                    "name": "abc",
                                    "password": "123"
                                }
                """).exchange();

        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("\"errors\":[");
        assertThat(body).contains("\"type\":\"PARAMETER\"");
        assertThat(body).contains("\"message\":\"Name length must be between 6-10\"");
        assertThat(body).doesNotContain("\"target\":");
    }
}

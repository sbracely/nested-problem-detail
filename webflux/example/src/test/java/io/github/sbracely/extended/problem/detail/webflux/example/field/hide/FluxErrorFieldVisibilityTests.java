package io.github.sbracely.extended.problem.detail.webflux.example.field.hide;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT1M")
@TestPropertySource(properties = "extended.problem-detail.field.hide[0]=errors.target")
class FluxErrorFieldVisibilityTests {

    private static final String BASE_PATH = "/flux-extended-problem-detail";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldHideConfiguredNestedErrorFields() {
        String body = webTestClient.post().uri(BASE_PATH + "/web-exchange-bind-exception")
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "abc",
                            "password": "123"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(body).contains("\"errors\":[");
        assertThat(body).contains("\"type\":\"PARAMETER\"");
        assertThat(body).contains("\"message\":\"Name length must be between 6-10\"");
        assertThat(body).doesNotContain("\"target\":");
    }
}

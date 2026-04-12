package io.github.sbracely.extended.problem.detail.webflux.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT1M")
@ActiveProfiles({"dev", "prod"})
@TestPropertySource(properties = {
        "extended.problem-detail.field.hide[0]=title",
        "extended.problem-detail.field.profiles.dev.hide[0]=status",
        "extended.problem-detail.field.profiles.prod.hide[0]=detail"
})
class FluxControllerFieldVisibilityTests {

    private static final String BASE_PATH = "/flux-extended-problem-detail";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldLetMatchingProfilesOverrideGlobalHides() {
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

        assertThat(body).contains("\"title\":\"Bad Request\"");
        assertThat(body).contains("\"instance\":\"/flux-extended-problem-detail/web-exchange-bind-exception\"");
        assertThat(body).contains("\"errors\":[");
        assertThat(body).doesNotContain("\"status\":");
        assertThat(body).doesNotContain("\"detail\":");
    }
}

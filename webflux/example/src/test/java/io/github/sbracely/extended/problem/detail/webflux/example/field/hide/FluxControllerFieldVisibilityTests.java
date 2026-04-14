package io.github.sbracely.extended.problem.detail.webflux.example.field.hide;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ALLOW;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT1M")
@TestPropertySource(properties = {
        "extended.problem-detail.field.hide[0]=status",
        "extended.problem-detail.field.hide[1]=detail",
        "extended.problem-detail.field.hide[2]=instance",
        "extended.problem-detail.field.hide[3]=errors"
})
class FluxControllerFieldVisibilityTests {

    private static final String BASE_PATH = "/flux-extended-problem-detail";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldHideConfiguredStandardFields() {
        String body = webTestClient.delete().uri(BASE_PATH + "/method-not-allowed-exception")
                .exchange()
                .expectStatus().isEqualTo(METHOD_NOT_ALLOWED)
                .expectHeader().valueEquals(ALLOW, GET.name())
                .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(body).contains("\"title\":\"Method Not Allowed\"");
        assertThat(body).doesNotContain("\"status\":");
        assertThat(body).doesNotContain("\"detail\":");
        assertThat(body).doesNotContain("\"instance\":");
        assertThat(body).doesNotContain("\"errors\":");
    }
}

package io.github.sbracely.extended.problem.detail.webmvc.example.controller;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MvcControllerRandomPortTests {

    private static final Logger logger = LoggerFactory.getLogger(MvcControllerRandomPortTests.class);
    private static final String BASE_PATH = "/mvc-extended-problem-detail";
    @LocalServerPort
    private int port;

    /**
     * @see AsyncRequestNotUsableException
     * @see MvcProblemDetailController#asyncRequestNotUsableException()
     */
    @Test
    void asyncRequestNotUsableException() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofMillis(1));

        RestClient restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl("http://localhost:" + port)
                .build();

        try {
            restClient.get()
                    .uri(BASE_PATH + "/async-request-not-usable-exception")
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Read timed out");
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            logger.error("Thread sleep interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}

package com.github.sbracely.extended.problem.detail.test.mvc.test.async;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AsyncRequestNotUsableExceptionTests {

    private static final String BASE_PATH = "/mvc-problem-detail";

    @LocalServerPort
    private int port;

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void asyncRequestNotUsableException(CapturedOutput output) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofMillis(1));

        RestClient restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl("http://localhost:" + port)
                .build();

        try {
            restClient.get()
                    .uri(BASE_PATH + "/async-request-not-usable")
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Read timed out");
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error("Thread sleep interrupted", e);
            Thread.currentThread().interrupt();
        }


        assertThat(output.getOut())
                .contains("handleAsyncRequestNotUsableException");
    }
}

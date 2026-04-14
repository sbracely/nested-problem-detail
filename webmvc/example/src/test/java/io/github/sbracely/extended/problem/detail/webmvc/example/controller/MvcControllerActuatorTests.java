package io.github.sbracely.extended.problem.detail.webmvc.example.controller;

import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "management.endpoints.web.exposure.include=demo")
class MvcControllerActuatorTests {

    @Autowired
    private MockMvcTester mockMvcTester;

    /**
     * Requires {@code management.endpoints.web.exposure.include=demo}; otherwise the actuator
     * endpoint is not exposed and this bad-request response cannot be triggered.
     *
     * @see ResponseStatusException
     */
    @Test
    void invalidEndpointBadRequestException() {
        String uri = "/actuator/demo/name";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();

        assertThat(result)
                .hasStatus(400)
                .hasContentType(APPLICATION_PROBLEM_JSON);

        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        assertThat(extendedProblemDetail).isNotNull();
        assertThat(extendedProblemDetail.getType()).isEqualTo(URI.create("about:blank"));
        assertThat(extendedProblemDetail.getTitle()).isEqualTo("Bad Request");
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(400);
        assertThat(extendedProblemDetail.getDetail())
                .startsWith("Missing parameters:")
                .contains("param1")
                .contains("param2");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getProperties()).isNull();
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }
}

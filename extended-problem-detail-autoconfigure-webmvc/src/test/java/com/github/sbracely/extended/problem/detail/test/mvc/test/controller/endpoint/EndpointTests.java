package com.github.sbracely.extended.problem.detail.test.mvc.test.controller.endpoint;

import com.github.sbracely.extended.problem.detail.response.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.test.mvc.ExtendedProblemDetailApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@Slf4j
@SpringBootTest(classes = ExtendedProblemDetailApplicationTest.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "management.endpoints.web.exposure.include=demo")
public class EndpointTests {
    @Autowired
    private MockMvcTester mockMvcTester;

    private static final String BASE_PATH = "/actuator";

    @Test
    void errorResponseExceptionAbstractWebMvcEndpointHandlerMappingInvalidEndpointBadRequestException() {
        String uri = BASE_PATH + "/demo/name";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(BAD_REQUEST)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getDetail()).containsOnlyOnce("Missing parameters: ")
                .contains("param1", "param2");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }
}

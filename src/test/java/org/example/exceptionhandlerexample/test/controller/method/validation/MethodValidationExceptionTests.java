package org.example.exceptionhandlerexample.test.controller.method.validation;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptionhandlerexample.config.MethodValidationConfiguration;
import org.example.exceptionhandlerexample.controller.MvcProblemDetailController;
import org.example.exceptionhandlerexample.response.NestedProblemDetail;
import org.example.exceptionhandlerexample.service.ProblemDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@Slf4j
@WebMvcTest(MvcProblemDetailController.class)
@Import({ProblemDetailService.class, MethodValidationConfiguration.class})
public class MethodValidationExceptionTests {
    @Autowired
    private MockMvcTester mockMvcTester;

    private static final String BASE_PATH = "/mvc-problem-detail";

    @Test
    void methodValidationException() {
        String uri = BASE_PATH + "/method-validation";
        MvcTestResult result = mockMvcTester.get().uri(uri).exchange();
        assertThat(result)
                .hasStatus(INTERNAL_SERVER_ERROR)
                .hasContentType(APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = assertThat(result).bodyJson()
                .convertTo(NestedProblemDetail.class).isNotNull().actual();
        log.info("nestedProblemDetail: {}", nestedProblemDetail);
        assertThat(nestedProblemDetail.getDetail()).isEqualTo("Validation failed");
        assertThat(nestedProblemDetail.getErrorCode()).isEqualTo("A00500");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(nestedProblemDetail.getErrors()).isNull();
    }
}

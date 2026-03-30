package com.github.sbracely.extended.problem.detail.test.mvc.test.controller.method.validation;

import com.github.sbracely.extended.problem.detail.ExtendedProblemDetailAutoConfiguration;
import com.github.sbracely.extended.problem.detail.response.ExtendedProblemDetail;
import com.github.sbracely.extended.problem.detail.test.mvc.config.MethodValidationConfiguration;
import com.github.sbracely.extended.problem.detail.test.mvc.controller.MvcProblemDetailController;
import com.github.sbracely.extended.problem.detail.test.mvc.service.ProblemDetailService;
import lombok.extern.slf4j.Slf4j;
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
@Import({ExtendedProblemDetailAutoConfiguration.class, ProblemDetailService.class, MethodValidationConfiguration.class})
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
        ExtendedProblemDetail extendedProblemDetail = assertThat(result).bodyJson()
                .convertTo(ExtendedProblemDetail.class).isNotNull().actual();
        log.info("extendedProblemDetail: {}", extendedProblemDetail);
        assertThat(extendedProblemDetail.getDetail()).isEqualTo("Validation failed");
        assertThat(extendedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(extendedProblemDetail.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(extendedProblemDetail.getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(extendedProblemDetail.getErrors()).isNull();
    }
}

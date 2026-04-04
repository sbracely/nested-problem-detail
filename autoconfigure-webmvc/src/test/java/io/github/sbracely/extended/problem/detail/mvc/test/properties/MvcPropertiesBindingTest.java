package io.github.sbracely.extended.problem.detail.mvc.test.properties;

import io.github.sbracely.extended.problem.detail.mvc.MvcExtendedProblemDetailProperties;
import io.github.sbracely.extended.problem.detail.mvc.MvcApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.logging.LogLevel;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link MvcExtendedProblemDetailProperties} binding.
 */
@SpringBootTest(classes = MvcApplicationTest.class)
@TestPropertySource(properties = {
        "extended.problem-detail.enabled=true",
        "extended.problem-detail.log-level=ERROR",
        "extended.problem-detail.print-stack-trace=true"
})
class MvcPropertiesBindingTest {

    @Autowired
    private MvcExtendedProblemDetailProperties properties;

    @Test
    void shouldBindEnabledProperty() {
        assertThat(properties.isEnabled()).isTrue();
    }

    @Test
    void shouldBindLogLevelProperty() {
        assertThat(properties.getLogLevel()).isEqualTo(LogLevel.ERROR);
    }

    @Test
    void shouldBindPrintStackTraceProperty() {
        assertThat(properties.isPrintStackTrace()).isTrue();
    }
}

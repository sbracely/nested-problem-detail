package com.github.sbracely.extended.problem.detail.test.flux.test.properties;

import com.github.sbracely.extended.problem.detail.flux.FluxExtendedProblemDetailProperties;
import com.github.sbracely.extended.problem.detail.test.flux.FluxApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.logging.LogLevel;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link FluxExtendedProblemDetailProperties} binding.
 */
@SpringBootTest(classes = FluxApplicationTest.class)
@TestPropertySource(properties = {
        "extended.problem-detail.enabled=true",
        "extended.problem-detail.log-level=WARN",
        "extended.problem-detail.print-stack-trace=true"
})
class FluxPropertiesBindingTests {

    @Autowired
    private FluxExtendedProblemDetailProperties properties;

    @Test
    void shouldBindEnabledProperty() {
        assertThat(properties.isEnabled()).isTrue();
    }

    @Test
    void shouldBindLogLevelProperty() {
        assertThat(properties.getLogLevel()).isEqualTo(LogLevel.WARN);
    }

    @Test
    void shouldBindPrintStackTraceProperty() {
        assertThat(properties.isPrintStackTrace()).isTrue();
    }
}

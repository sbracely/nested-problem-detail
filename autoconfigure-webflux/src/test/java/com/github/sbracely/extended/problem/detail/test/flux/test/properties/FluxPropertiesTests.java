package com.github.sbracely.extended.problem.detail.test.flux.test.properties;

import com.github.sbracely.extended.problem.detail.flux.FluxExtendedProblemDetailProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link FluxExtendedProblemDetailProperties} class.
 */
class FluxPropertiesTests {

    @Test
    void shouldHaveDefaultEnabledValue() {
        FluxExtendedProblemDetailProperties properties = new FluxExtendedProblemDetailProperties();

        assertThat(properties.isEnabled()).isTrue();
    }

    @Test
    void shouldHaveDefaultLogLevelValue() {
        FluxExtendedProblemDetailProperties properties = new FluxExtendedProblemDetailProperties();

        assertThat(properties.getLogLevel()).isEqualTo(LogLevel.DEBUG);
    }

    @Test
    void shouldHaveDefaultPrintStackTraceValue() {
        FluxExtendedProblemDetailProperties properties = new FluxExtendedProblemDetailProperties();

        assertThat(properties.isPrintStackTrace()).isFalse();
    }

    @Test
    void shouldSetEnabledValue() {
        FluxExtendedProblemDetailProperties properties = new FluxExtendedProblemDetailProperties();

        properties.setEnabled(false);

        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    void shouldSetLogLevelValue() {
        FluxExtendedProblemDetailProperties properties = new FluxExtendedProblemDetailProperties();

        properties.setLogLevel(LogLevel.ERROR);

        assertThat(properties.getLogLevel()).isEqualTo(LogLevel.ERROR);
    }

    @Test
    void shouldSetPrintStackTraceValue() {
        FluxExtendedProblemDetailProperties properties = new FluxExtendedProblemDetailProperties();

        properties.setPrintStackTrace(true);

        assertThat(properties.isPrintStackTrace()).isTrue();
    }

    @Test
    void shouldSupportAllLogLevels() {
        FluxExtendedProblemDetailProperties properties = new FluxExtendedProblemDetailProperties();

        for (LogLevel level : LogLevel.values()) {
            properties.setLogLevel(level);
            assertThat(properties.getLogLevel()).isEqualTo(level);
        }
    }
}

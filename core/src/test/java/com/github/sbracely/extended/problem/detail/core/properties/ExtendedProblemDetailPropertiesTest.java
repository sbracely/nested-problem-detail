package com.github.sbracely.extended.problem.detail.core.properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ExtendedProblemDetailProperties} class.
 */
class ExtendedProblemDetailPropertiesTest {

    @Test
    void shouldHaveDefaultEnabledValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        assertThat(properties.isEnabled()).isTrue();
    }

    @Test
    void shouldHaveDefaultLogLevelValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        assertThat(properties.getLogLevel()).isEqualTo(LogLevel.DEBUG);
    }

    @Test
    void shouldHaveDefaultPrintStackTraceValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        assertThat(properties.isPrintStackTrace()).isFalse();
    }

    @Test
    void shouldSetEnabledValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        properties.setEnabled(false);

        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    void shouldSetLogLevelValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        properties.setLogLevel(LogLevel.ERROR);

        assertThat(properties.getLogLevel()).isEqualTo(LogLevel.ERROR);
    }

    @Test
    void shouldSetPrintStackTraceValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        properties.setPrintStackTrace(true);

        assertThat(properties.isPrintStackTrace()).isTrue();
    }

    @Test
    void shouldSupportAllLogLevels() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        for (LogLevel level : LogLevel.values()) {
            properties.setLogLevel(level);
            assertThat(properties.getLogLevel()).isEqualTo(level);
        }
    }
}

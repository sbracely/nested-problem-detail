package io.github.sbracely.extended.problem.detail.mvc.test.properties;

import io.github.sbracely.extended.problem.detail.mvc.MvcExtendedProblemDetailProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MvcExtendedProblemDetailProperties} class.
 */
class MvcPropertiesTest {

    @Test
    void shouldHaveDefaultEnabledValue() {
        MvcExtendedProblemDetailProperties properties = new MvcExtendedProblemDetailProperties();

        assertThat(properties.isEnabled()).isTrue();
    }

    @Test
    void shouldHaveDefaultLogLevelValue() {
        MvcExtendedProblemDetailProperties properties = new MvcExtendedProblemDetailProperties();

        assertThat(properties.getLogLevel()).isEqualTo(LogLevel.DEBUG);
    }

    @Test
    void shouldHaveDefaultPrintStackTraceValue() {
        MvcExtendedProblemDetailProperties properties = new MvcExtendedProblemDetailProperties();

        assertThat(properties.isPrintStackTrace()).isFalse();
    }

    @Test
    void shouldSetEnabledValue() {
        MvcExtendedProblemDetailProperties properties = new MvcExtendedProblemDetailProperties();

        properties.setEnabled(false);

        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    void shouldSetLogLevelValue() {
        MvcExtendedProblemDetailProperties properties = new MvcExtendedProblemDetailProperties();

        properties.setLogLevel(LogLevel.ERROR);

        assertThat(properties.getLogLevel()).isEqualTo(LogLevel.ERROR);
    }

    @Test
    void shouldSetPrintStackTraceValue() {
        MvcExtendedProblemDetailProperties properties = new MvcExtendedProblemDetailProperties();

        properties.setPrintStackTrace(true);

        assertThat(properties.isPrintStackTrace()).isTrue();
    }

    @Test
    void shouldSupportAllLogLevels() {
        MvcExtendedProblemDetailProperties properties = new MvcExtendedProblemDetailProperties();

        for (LogLevel level : LogLevel.values()) {
            properties.setLogLevel(level);
            assertThat(properties.getLogLevel()).isEqualTo(level);
        }
    }
}

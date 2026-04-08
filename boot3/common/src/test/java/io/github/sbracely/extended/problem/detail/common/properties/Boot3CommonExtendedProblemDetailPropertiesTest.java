package io.github.sbracely.extended.problem.detail.common.properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Boot3CommonExtendedProblemDetailProperties} class.
 */
class Boot3CommonExtendedProblemDetailPropertiesTest {

    @Test
    void shouldHaveDefaultEnabledValue() {
        Boot3CommonExtendedProblemDetailProperties properties = new Boot3CommonExtendedProblemDetailProperties();

        assertThat(properties.isEnabled()).isTrue();
    }

    @Test
    void shouldHaveDefaultLogAtLevelValue() {
        Boot3CommonExtendedProblemDetailProperties properties = new Boot3CommonExtendedProblemDetailProperties();

        assertThat(properties.getLogging().getAtLevel()).isEqualTo(LogLevel.INFO);
    }

    @Test
    void shouldHaveDefaultPrintStackTraceValue() {
        Boot3CommonExtendedProblemDetailProperties properties = new Boot3CommonExtendedProblemDetailProperties();

        assertThat(properties.getLogging().isPrintStackTrace()).isFalse();
    }

    @Test
    void shouldSetEnabledValue() {
        Boot3CommonExtendedProblemDetailProperties properties = new Boot3CommonExtendedProblemDetailProperties();

        properties.setEnabled(false);

        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    void shouldSetLogAtLevelValue() {
        Boot3CommonExtendedProblemDetailProperties properties = new Boot3CommonExtendedProblemDetailProperties();

        properties.getLogging().setAtLevel(LogLevel.ERROR);

        assertThat(properties.getLogging().getAtLevel()).isEqualTo(LogLevel.ERROR);
    }

    @Test
    void shouldSetPrintStackTraceValue() {
        Boot3CommonExtendedProblemDetailProperties properties = new Boot3CommonExtendedProblemDetailProperties();

        properties.getLogging().setPrintStackTrace(true);

        assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
    }

    @Test
    void shouldSupportAllLogLevels() {
        Boot3CommonExtendedProblemDetailProperties properties = new Boot3CommonExtendedProblemDetailProperties();

        for (LogLevel level : LogLevel.values()) {
            properties.getLogging().setAtLevel(level);
            assertThat(properties.getLogging().getAtLevel()).isEqualTo(level);
        }
    }
}

package io.github.sbracely.extended.problem.detail.common.properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Boot4CommonExtendedProblemDetailProperties} class.
 */
class Boot4CommonExtendedProblemDetailPropertiesTest {

    @Test
    void shouldHaveDefaultEnabledValue() {
        Boot4CommonExtendedProblemDetailProperties properties = new Boot4CommonExtendedProblemDetailProperties();

        assertThat(properties.isEnabled()).isTrue();
    }

    @Test
    void shouldHaveDefaultLogAtLevelValue() {
        Boot4CommonExtendedProblemDetailProperties properties = new Boot4CommonExtendedProblemDetailProperties();

        assertThat(properties.getLogging().getAtLevel()).isEqualTo(LogLevel.INFO);
    }

    @Test
    void shouldHaveDefaultPrintStackTraceValue() {
        Boot4CommonExtendedProblemDetailProperties properties = new Boot4CommonExtendedProblemDetailProperties();

        assertThat(properties.getLogging().isPrintStackTrace()).isFalse();
    }

    @Test
    void shouldSetEnabledValue() {
        Boot4CommonExtendedProblemDetailProperties properties = new Boot4CommonExtendedProblemDetailProperties();

        properties.setEnabled(false);

        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    void shouldSetLogAtLevelValue() {
        Boot4CommonExtendedProblemDetailProperties properties = new Boot4CommonExtendedProblemDetailProperties();

        properties.getLogging().setAtLevel(LogLevel.ERROR);

        assertThat(properties.getLogging().getAtLevel()).isEqualTo(LogLevel.ERROR);
    }

    @Test
    void shouldSetPrintStackTraceValue() {
        Boot4CommonExtendedProblemDetailProperties properties = new Boot4CommonExtendedProblemDetailProperties();

        properties.getLogging().setPrintStackTrace(true);

        assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
    }

    @Test
    void shouldSupportAllLogLevels() {
        Boot4CommonExtendedProblemDetailProperties properties = new Boot4CommonExtendedProblemDetailProperties();

        for (LogLevel level : LogLevel.values()) {
            properties.getLogging().setAtLevel(level);
            assertThat(properties.getLogging().getAtLevel()).isEqualTo(level);
        }
    }
}

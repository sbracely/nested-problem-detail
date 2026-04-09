package io.github.sbracely.extended.problem.detail.common.properties;

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
    void shouldHaveDefaultLogAtLevelValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        assertThat(properties.getLogging().getAtLevel()).isEqualTo(LogLevel.INFO);
    }

    @Test
    void shouldHaveDefaultPrintStackTraceValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        assertThat(properties.getLogging().isPrintStackTrace()).isFalse();
    }

    @Test
    void shouldSetEnabledValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        properties.setEnabled(false);

        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    void shouldSetLogAtLevelValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        properties.getLogging().setAtLevel(LogLevel.ERROR);

        assertThat(properties.getLogging().getAtLevel()).isEqualTo(LogLevel.ERROR);
    }

    @Test
    void shouldSetPrintStackTraceValue() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        properties.getLogging().setPrintStackTrace(true);

        assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
    }

    @Test
    void shouldSupportAllLogLevels() {
        ExtendedProblemDetailProperties properties = new ExtendedProblemDetailProperties();

        for (LogLevel level : LogLevel.values()) {
            properties.getLogging().setAtLevel(level);
            assertThat(properties.getLogging().getAtLevel()).isEqualTo(level);
        }
    }
}

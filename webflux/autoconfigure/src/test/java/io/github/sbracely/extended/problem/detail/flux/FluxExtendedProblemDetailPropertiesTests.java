package io.github.sbracely.extended.problem.detail.flux;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FluxExtendedProblemDetailProperties}.
 *
 * @since 1.0.0
 */
class FluxExtendedProblemDetailPropertiesTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(FluxExtendedProblemDetailPropertiesConfiguration.class);

    @Test
    void shouldHaveDefaultValues() {
        this.contextRunner.run(context -> {
            FluxExtendedProblemDetailProperties properties = context.getBean(FluxExtendedProblemDetailProperties.class);
            assertThat(properties.isEnabled()).isTrue();
            assertThat(properties.getLogging().getAtLevel()).isEqualTo(LogLevel.INFO);
            assertThat(properties.getLogging().isPrintStackTrace()).isFalse();
        });
    }

    @Test
    void shouldBindEnabledProperty() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.enabled=false")
                .run(context -> {
                    FluxExtendedProblemDetailProperties properties = context.getBean(FluxExtendedProblemDetailProperties.class);
                    assertThat(properties.isEnabled()).isFalse();
                });
    }

    @Test
    void shouldBindLogAtLevelProperty() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.logging.at-level=INFO")
                .run(context -> {
                    FluxExtendedProblemDetailProperties properties = context.getBean(FluxExtendedProblemDetailProperties.class);
                    assertThat(properties.getLogging().getAtLevel()).isEqualTo(LogLevel.INFO);
                });
    }

    @Test
    void shouldBindPrintStackTraceProperty() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.logging.print-stack-trace=true")
                .run(context -> {
                    FluxExtendedProblemDetailProperties properties = context.getBean(FluxExtendedProblemDetailProperties.class);
                    assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
                });
    }

    @Test
    void shouldBindAllProperties() {
        this.contextRunner
                .withPropertyValues(
                        "extended.problem-detail.enabled=false",
                        "extended.problem-detail.logging.at-level=ERROR",
                        "extended.problem-detail.logging.print-stack-trace=true"
                )
                .run(context -> {
                    FluxExtendedProblemDetailProperties properties = context.getBean(FluxExtendedProblemDetailProperties.class);
                    assertThat(properties.isEnabled()).isFalse();
                    assertThat(properties.getLogging().getAtLevel()).isEqualTo(LogLevel.ERROR);
                    assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
                });
    }

    @Configuration
    @EnableConfigurationProperties(FluxExtendedProblemDetailProperties.class)
    static class FluxExtendedProblemDetailPropertiesConfiguration {
    }
}

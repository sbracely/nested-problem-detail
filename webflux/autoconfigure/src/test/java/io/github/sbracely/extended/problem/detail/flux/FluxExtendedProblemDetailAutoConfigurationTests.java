package io.github.sbracely.extended.problem.detail.flux;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailStartupLogger;
import io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties;
import io.github.sbracely.extended.problem.detail.flux.advice.FluxExtendedProblemDetailExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FluxExtendedProblemDetailAutoConfiguration}.
 *
 * @since 1.0.0
 */
@ExtendWith(OutputCaptureExtension.class)
class FluxExtendedProblemDetailAutoConfigurationTests {

    private final ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FluxExtendedProblemDetailAutoConfiguration.class));

    @Test
    void shouldAutoConfigureBeans() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(FluxExtendedProblemDetailProperties.class);
            assertThat(context).hasSingleBean(ExtendedProblemDetailLog.class);
            assertThat(context).hasSingleBean(ExtendedProblemDetailStartupLogger.class);
            assertThat(context).hasSingleBean(FluxExtendedProblemDetailExceptionHandler.class);
        });
    }

    @Test
    void shouldNotConfigureWhenDisabled() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(FluxExtendedProblemDetailProperties.class);
                    assertThat(context).doesNotHaveBean(ExtendedProblemDetailLog.class);
                    assertThat(context).doesNotHaveBean(ExtendedProblemDetailStartupLogger.class);
                    assertThat(context).doesNotHaveBean(FluxExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldLogWhenEnabledByDefault(CapturedOutput output) {
        ExtendedProblemDetailProperties.CommonLogging loggingDefaults =
                new ExtendedProblemDetailProperties.CommonLogging();
        this.contextRunner.run(context -> {
            context.publishEvent(new ApplicationReadyEvent(
                    new SpringApplication(FluxExtendedProblemDetailAutoConfiguration.class),
                    new String[0],
                    context,
                    Duration.ZERO));
            assertThat(output)
                    .contains("Extended Problem Detail is enabled by default for Spring WebFlux")
                    .contains("Logging: extended.problem-detail.logging.at-level=" + loggingDefaults.getAtLevel() + ", "
                            + "extended.problem-detail.logging.print-stack-trace="
                            + loggingDefaults.isPrintStackTrace())
                    .contains("To disable it, set 'extended.problem-detail.enabled=false'");
            assertThat(output).contains("extended.problem-detail.enabled=false");
        });
    }

    @Test
    void shouldLogEffectiveLoggingSettingsWhenEnabledByDefault(CapturedOutput output) {
        this.contextRunner
                .withPropertyValues(
                        "extended.problem-detail.logging.at-level=WARN",
                        "extended.problem-detail.logging.print-stack-trace=true")
                .run(context -> {
                    context.publishEvent(new ApplicationReadyEvent(
                            new SpringApplication(FluxExtendedProblemDetailAutoConfiguration.class),
                            new String[0],
                            context,
                            Duration.ZERO));
                    assertThat(output)
                            .contains("Extended Problem Detail is enabled by default for Spring WebFlux")
                            .contains("Logging: extended.problem-detail.logging.at-level=WARN, "
                                    + "extended.problem-detail.logging.print-stack-trace=true");
                });
    }

    @Test
    void shouldNotLogWhenEnabledPropertyIsExplicit(CapturedOutput output) {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.enabled=true")
                .run(context -> {
                    context.publishEvent(new ApplicationReadyEvent(
                            new SpringApplication(FluxExtendedProblemDetailAutoConfiguration.class),
                            new String[0],
                            context,
                            Duration.ZERO));
                    assertThat(output).doesNotContain("Extended Problem Detail is enabled by default for Spring WebFlux");
                });
    }

    @Test
    void shouldNotConfigureExceptionHandlerWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(FluxCustomExceptionHandlerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(FluxExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(FluxExtendedProblemDetailExceptionHandler.class))
                            .isInstanceOf(FluxCustomFluxExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureLogWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(FluxCustomLogConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ExtendedProblemDetailLog.class);
                    assertThat(context.getBean(ExtendedProblemDetailLog.class))
                            .isSameAs(FluxCustomLogConfiguration.customLog);
                });
    }

    @Test
    void shouldConfigureWithCustomProperties() {
        this.contextRunner
                .withPropertyValues(
                        "extended.problem-detail.logging.at-level=WARN",
                        "extended.problem-detail.logging.print-stack-trace=true"
                )
                .run(context -> {
                    FluxExtendedProblemDetailProperties properties = context.getBean(FluxExtendedProblemDetailProperties.class);
                    assertThat(properties.getLogging().getAtLevel().name()).isEqualTo("WARN");
                    assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
                });
    }

    @Test
    void shouldNotConfigureLogBeanWhenLevelIsOff() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.logging.at-level=OFF")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ExtendedProblemDetailLog.class);
                    assertThat(context).hasSingleBean(FluxExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(FluxExtendedProblemDetailExceptionHandler.class).getExtendedProblemDetailLog())
                            .isNull();
                });
    }

    @Configuration
    static class FluxCustomExceptionHandlerConfiguration {

        @Bean
        FluxExtendedProblemDetailExceptionHandler customFluxExtendedProblemDetailExceptionHandler() {
            return new FluxCustomFluxExtendedProblemDetailExceptionHandler(
                    new ExtendedProblemDetailLog(org.springframework.boot.logging.LogLevel.DEBUG, false));
        }
    }

    @Configuration
    static class FluxCustomLogConfiguration {

        static final ExtendedProblemDetailLog customLog = new ExtendedProblemDetailLog(
                org.springframework.boot.logging.LogLevel.INFO, true);

        @Bean
        ExtendedProblemDetailLog customExtendedProblemDetailLog() {
            return customLog;
        }
    }

    static class FluxCustomFluxExtendedProblemDetailExceptionHandler extends FluxExtendedProblemDetailExceptionHandler {

        FluxCustomFluxExtendedProblemDetailExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
            super(extendedProblemDetailLog);
        }
    }
}

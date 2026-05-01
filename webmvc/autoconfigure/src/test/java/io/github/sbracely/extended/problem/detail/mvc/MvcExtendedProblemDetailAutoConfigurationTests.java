package io.github.sbracely.extended.problem.detail.mvc;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailStartupLogger;
import io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties;
import io.github.sbracely.extended.problem.detail.mvc.advice.MvcExtendedProblemDetailExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MvcExtendedProblemDetailAutoConfiguration}.
 *
 * @since 1.0.0
 */
@ExtendWith(OutputCaptureExtension.class)
class MvcExtendedProblemDetailAutoConfigurationTests {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MvcExtendedProblemDetailAutoConfiguration.class));

    @Test
    void shouldAutoConfigureBeans() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MvcExtendedProblemDetailProperties.class);
            assertThat(context).hasSingleBean(ExtendedProblemDetailLog.class);
            assertThat(context).hasSingleBean(ExtendedProblemDetailStartupLogger.class);
            assertThat(context).hasSingleBean(MvcExtendedProblemDetailExceptionHandler.class);
        });
    }

    @Test
    void shouldNotConfigureWhenDisabled() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(MvcExtendedProblemDetailProperties.class);
                    assertThat(context).doesNotHaveBean(ExtendedProblemDetailLog.class);
                    assertThat(context).doesNotHaveBean(ExtendedProblemDetailStartupLogger.class);
                    assertThat(context).doesNotHaveBean(MvcExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldLogWhenEnabledByDefault(CapturedOutput output) {
        ExtendedProblemDetailProperties.CommonLogging loggingDefaults =
                new ExtendedProblemDetailProperties.CommonLogging();
        this.contextRunner.run(context -> {
            context.publishEvent(new ApplicationReadyEvent(
                    new SpringApplication(MvcExtendedProblemDetailAutoConfiguration.class),
                    new String[0],
                    context,
                    Duration.ZERO));
            assertThat(output)
                    .contains("Extended Problem Detail is enabled by default for Spring WebMVC")
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
                            new SpringApplication(MvcExtendedProblemDetailAutoConfiguration.class),
                            new String[0],
                            context,
                            Duration.ZERO));
                    assertThat(output)
                            .contains("Extended Problem Detail is enabled by default for Spring WebMVC")
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
                            new SpringApplication(MvcExtendedProblemDetailAutoConfiguration.class),
                            new String[0],
                            context,
                            Duration.ZERO));
                    assertThat(output).doesNotContain("Extended Problem Detail is enabled by default for Spring WebMVC");
                });
    }

    @Test
    void shouldNotConfigureExceptionHandlerWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(MvcCustomExceptionHandlerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(MvcExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(MvcExtendedProblemDetailExceptionHandler.class))
                            .isInstanceOf(MvcCustomMvcExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureLogWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(MvcCustomLogConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ExtendedProblemDetailLog.class);
                    assertThat(context.getBean(ExtendedProblemDetailLog.class))
                            .isSameAs(MvcCustomLogConfiguration.customLog);
                });
    }

    @Test
    void shouldConfigureWithCustomProperties() {
        this.contextRunner
                .withPropertyValues(
                        "extended.problem-detail.errors-property-name=violations",
                        "extended.problem-detail.logging.at-level=WARN",
                        "extended.problem-detail.logging.print-stack-trace=true"
                )
                .run(context -> {
                    MvcExtendedProblemDetailProperties properties = context.getBean(MvcExtendedProblemDetailProperties.class);
                    MvcExtendedProblemDetailExceptionHandler handler = context.getBean(MvcExtendedProblemDetailExceptionHandler.class);
                    assertThat(properties.getErrorsPropertyName()).isEqualTo("violations");
                    assertThat(properties.getLogging().getAtLevel().name()).isEqualTo("WARN");
                    assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
                    assertThat(ReflectionTestUtils.getField(handler, "errorsPropertyName")).isEqualTo("violations");
                });
    }

    @Test
    void shouldNotConfigureLogBeanWhenLevelIsOff() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.logging.at-level=OFF")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ExtendedProblemDetailLog.class);
                    assertThat(context).hasSingleBean(MvcExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(MvcExtendedProblemDetailExceptionHandler.class).getExtendedProblemDetailLog())
                            .isNull();
                });
    }

    @Configuration
    static class MvcCustomExceptionHandlerConfiguration {

        @Bean
        MvcExtendedProblemDetailExceptionHandler customMvcExtendedProblemDetailExceptionHandler() {
            return new MvcCustomMvcExtendedProblemDetailExceptionHandler(
                    new ExtendedProblemDetailLog(org.springframework.boot.logging.LogLevel.DEBUG, false));
        }
    }

    @Configuration
    static class MvcCustomLogConfiguration {

        static final ExtendedProblemDetailLog customLog = new ExtendedProblemDetailLog(
                org.springframework.boot.logging.LogLevel.INFO, true);

        @Bean
        ExtendedProblemDetailLog customExtendedProblemDetailLog() {
            return customLog;
        }
    }

    static class MvcCustomMvcExtendedProblemDetailExceptionHandler extends MvcExtendedProblemDetailExceptionHandler {

        MvcCustomMvcExtendedProblemDetailExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
            super(extendedProblemDetailLog, ExtendedProblemDetailProperties.DEFAULT_ERRORS_PROPERTY_NAME);
        }
    }
}

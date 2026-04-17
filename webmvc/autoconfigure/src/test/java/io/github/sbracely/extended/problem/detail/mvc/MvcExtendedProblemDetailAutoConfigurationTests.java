package io.github.sbracely.extended.problem.detail.mvc;

import io.github.sbracely.extended.problem.detail.common.field.hide.ProblemDetailFieldVisibility;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailStartupLogger;
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
import tools.jackson.databind.JacksonModule;

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
            assertThat(context).doesNotHaveBean(ProblemDetailFieldVisibility.class);
            assertThat(context).doesNotHaveBean(JacksonModule.class);
            assertThat(context).doesNotHaveBean("extendedProblemDetailJacksonModule");
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
        this.contextRunner.run(context -> {
            context.publishEvent(new ApplicationReadyEvent(
                    new SpringApplication(MvcExtendedProblemDetailAutoConfiguration.class),
                    new String[0],
                    context,
                    Duration.ZERO));
            assertThat(output).contains("Extended Problem Detail is enabled by default for Spring WebMVC");
            assertThat(output).contains("extended.problem-detail.enabled=false");
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
                        "extended.problem-detail.logging.at-level=WARN",
                        "extended.problem-detail.logging.print-stack-trace=true"
                )
                .run(context -> {
                    MvcExtendedProblemDetailProperties properties = context.getBean(MvcExtendedProblemDetailProperties.class);
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
                    assertThat(context).doesNotHaveBean(JacksonModule.class);
                    assertThat(context).hasSingleBean(MvcExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(MvcExtendedProblemDetailExceptionHandler.class).getExtendedProblemDetailLog())
                            .isNull();
                });
    }

    @Test
    void shouldCreateFieldVisibilityFromActiveProfiles() {
        this.contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dev,prod",
                        "extended.problem-detail.field.hide[0]=title",
                        "extended.problem-detail.field.profiles.dev.hide[0]=status",
                        "extended.problem-detail.field.profiles.prod.hide[0]=detail",
                        "extended.problem-detail.field.profiles.prod.hide[1]=errors.target"
                )
                .run(context -> {
                    ProblemDetailFieldVisibility fieldVisibility = context.getBean(ProblemDetailFieldVisibility.class);
                    assertThat(context).hasSingleBean(JacksonModule.class);
                    assertThat(context).hasBean("extendedProblemDetailJacksonModule");
                    assertThat(fieldVisibility.isVisible("errors")).isTrue();
                    assertThat(fieldVisibility.isVisible("title")).isTrue();
                    assertThat(fieldVisibility.isVisible("status")).isFalse();
                    assertThat(fieldVisibility.isVisible("detail")).isFalse();
                    assertThat(fieldVisibility.isErrorFieldVisible("target")).isFalse();
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
            super(extendedProblemDetailLog);
        }
    }
}

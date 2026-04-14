package io.github.sbracely.extended.problem.detail.flux;

import io.github.sbracely.extended.problem.detail.common.field.hide.ProblemDetailFieldVisibility;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.flux.advice.FluxExtendedProblemDetailExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.JacksonModule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FluxExtendedProblemDetailAutoConfiguration}.
 *
 * @since 1.0.0
 */
class FluxExtendedProblemDetailAutoConfigurationTests {

    private final ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FluxExtendedProblemDetailAutoConfiguration.class));

    @Test
    void shouldAutoConfigureBeans() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(FluxExtendedProblemDetailProperties.class);
            assertThat(context).hasSingleBean(ExtendedProblemDetailLog.class);
            assertThat(context).hasSingleBean(ProblemDetailFieldVisibility.class);
            assertThat(context).hasSingleBean(JacksonModule.class);
            assertThat(context).hasBean("extendedProblemDetailJacksonModule");
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
                    assertThat(context).doesNotHaveBean(FluxExtendedProblemDetailExceptionHandler.class);
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
    void shouldCreateFieldVisibilityFromActiveProfiles() {
        this.contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dev,prod",
                        "extended.problem-detail.field.hide[0]=title",
                        "extended.problem-detail.field.profiles.dev.hide[0]=status",
                        "extended.problem-detail.field.profiles.prod.hide[0]=detail"
                )
                .run(context -> {
                    ProblemDetailFieldVisibility fieldVisibility = context.getBean(ProblemDetailFieldVisibility.class);
                    assertThat(fieldVisibility.isVisible("errors")).isTrue();
                    assertThat(fieldVisibility.isVisible("title")).isTrue();
                    assertThat(fieldVisibility.isVisible("status")).isFalse();
                    assertThat(fieldVisibility.isVisible("detail")).isFalse();
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

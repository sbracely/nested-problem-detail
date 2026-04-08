package io.github.sbracely.extended.problem.detail.flux;

import io.github.sbracely.extended.problem.detail.common.logging.Boot3CommonExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.flux.handler.Boot3FluxExtendedProblemDetailExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Boot3FluxExtendedProblemDetailAutoConfiguration}.
 *
 * @since 1.0.0
 */
class Boot3FluxExtendedProblemDetailAutoConfigurationTests {

    private final ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Boot3FluxExtendedProblemDetailAutoConfiguration.class));

    @Test
    void shouldAutoConfigureBeans() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(Boot3FluxExtendedProblemDetailProperties.class);
            assertThat(context).hasSingleBean(Boot3CommonExtendedProblemDetailLog.class);
            assertThat(context).hasSingleBean(Boot3FluxExtendedProblemDetailExceptionHandler.class);
        });
    }

    @Test
    void shouldNotConfigureWhenDisabled() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(Boot3FluxExtendedProblemDetailProperties.class);
                    assertThat(context).doesNotHaveBean(Boot3CommonExtendedProblemDetailLog.class);
                    assertThat(context).doesNotHaveBean(Boot3FluxExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureExceptionHandlerWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(Boot3FluxCustomExceptionHandlerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(Boot3FluxExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(Boot3FluxExtendedProblemDetailExceptionHandler.class))
                            .isInstanceOf(Boot3FluxCustomFluxExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureLogWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(Boot3FluxCustomLogConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(Boot3CommonExtendedProblemDetailLog.class);
                    assertThat(context.getBean(Boot3CommonExtendedProblemDetailLog.class))
                            .isSameAs(Boot3FluxCustomLogConfiguration.customLog);
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
                    Boot3FluxExtendedProblemDetailProperties properties = context.getBean(Boot3FluxExtendedProblemDetailProperties.class);
                    assertThat(properties.getLogging().getAtLevel().name()).isEqualTo("WARN");
                    assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
                });
    }

    @Configuration
    static class Boot3FluxCustomExceptionHandlerConfiguration {

        @Bean
        Boot3FluxExtendedProblemDetailExceptionHandler customFluxExtendedProblemDetailExceptionHandler() {
            return new Boot3FluxCustomFluxExtendedProblemDetailExceptionHandler(
                    new Boot3CommonExtendedProblemDetailLog(org.springframework.boot.logging.LogLevel.DEBUG, false));
        }
    }

    @Configuration
    static class Boot3FluxCustomLogConfiguration {

        static final Boot3CommonExtendedProblemDetailLog customLog = new Boot3CommonExtendedProblemDetailLog(
                org.springframework.boot.logging.LogLevel.INFO, true);

        @Bean
        Boot3CommonExtendedProblemDetailLog customExtendedProblemDetailLog() {
            return customLog;
        }
    }

    static class Boot3FluxCustomFluxExtendedProblemDetailExceptionHandler extends Boot3FluxExtendedProblemDetailExceptionHandler {

        Boot3FluxCustomFluxExtendedProblemDetailExceptionHandler(Boot3CommonExtendedProblemDetailLog extendedProblemDetailLog) {
            super(extendedProblemDetailLog);
        }
    }
}

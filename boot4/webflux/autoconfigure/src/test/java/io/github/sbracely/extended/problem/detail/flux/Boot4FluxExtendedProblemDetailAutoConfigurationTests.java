package io.github.sbracely.extended.problem.detail.flux;

import io.github.sbracely.extended.problem.detail.common.logging.Boot4CommonExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.flux.handler.Boot4FluxExtendedProblemDetailExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Boot4FluxExtendedProblemDetailAutoConfiguration}.
 *
 * @since 1.0.0
 */
class Boot4FluxExtendedProblemDetailAutoConfigurationTests {

    private final ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Boot4FluxExtendedProblemDetailAutoConfiguration.class));

    @Test
    void shouldAutoConfigureBeans() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(Boot4FluxExtendedProblemDetailProperties.class);
            assertThat(context).hasSingleBean(Boot4CommonExtendedProblemDetailLog.class);
            assertThat(context).hasSingleBean(Boot4FluxExtendedProblemDetailExceptionHandler.class);
        });
    }

    @Test
    void shouldNotConfigureWhenDisabled() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(Boot4FluxExtendedProblemDetailProperties.class);
                    assertThat(context).doesNotHaveBean(Boot4CommonExtendedProblemDetailLog.class);
                    assertThat(context).doesNotHaveBean(Boot4FluxExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureExceptionHandlerWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(Boot4FluxCustomExceptionHandlerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(Boot4FluxExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(Boot4FluxExtendedProblemDetailExceptionHandler.class))
                            .isInstanceOf(Boot4FluxCustomFluxExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureLogWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(Boot4FluxCustomLogConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(Boot4CommonExtendedProblemDetailLog.class);
                    assertThat(context.getBean(Boot4CommonExtendedProblemDetailLog.class))
                            .isSameAs(Boot4FluxCustomLogConfiguration.customLog);
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
                    Boot4FluxExtendedProblemDetailProperties properties = context.getBean(Boot4FluxExtendedProblemDetailProperties.class);
                    assertThat(properties.getLogging().getAtLevel().name()).isEqualTo("WARN");
                    assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
                });
    }

    @Configuration
    static class Boot4FluxCustomExceptionHandlerConfiguration {

        @Bean
        Boot4FluxExtendedProblemDetailExceptionHandler customFluxExtendedProblemDetailExceptionHandler() {
            return new Boot4FluxCustomFluxExtendedProblemDetailExceptionHandler(
                    new Boot4CommonExtendedProblemDetailLog(org.springframework.boot.logging.LogLevel.DEBUG, false));
        }
    }

    @Configuration
    static class Boot4FluxCustomLogConfiguration {

        static final Boot4CommonExtendedProblemDetailLog customLog = new Boot4CommonExtendedProblemDetailLog(
                org.springframework.boot.logging.LogLevel.INFO, true);

        @Bean
        Boot4CommonExtendedProblemDetailLog customExtendedProblemDetailLog() {
            return customLog;
        }
    }

    static class Boot4FluxCustomFluxExtendedProblemDetailExceptionHandler extends Boot4FluxExtendedProblemDetailExceptionHandler {

        Boot4FluxCustomFluxExtendedProblemDetailExceptionHandler(Boot4CommonExtendedProblemDetailLog extendedProblemDetailLog) {
            super(extendedProblemDetailLog);
        }
    }
}

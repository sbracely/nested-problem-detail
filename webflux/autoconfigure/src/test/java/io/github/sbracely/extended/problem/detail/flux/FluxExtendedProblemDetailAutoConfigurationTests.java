package io.github.sbracely.extended.problem.detail.flux;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.flux.handler.FluxExtendedProblemDetailExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .withUserConfiguration(CustomExceptionHandlerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(FluxExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(FluxExtendedProblemDetailExceptionHandler.class))
                            .isInstanceOf(CustomFluxExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureLogWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(CustomLogConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ExtendedProblemDetailLog.class);
                    assertThat(context.getBean(ExtendedProblemDetailLog.class))
                            .isSameAs(CustomLogConfiguration.customLog);
                });
    }

    @Test
    void shouldConfigureWithCustomProperties() {
        this.contextRunner
                .withPropertyValues(
                        "extended.problem-detail.log-level=WARN",
                        "extended.problem-detail.print-stack-trace=true"
                )
                .run(context -> {
                    FluxExtendedProblemDetailProperties properties = context.getBean(FluxExtendedProblemDetailProperties.class);
                    assertThat(properties.getLogLevel().name()).isEqualTo("WARN");
                    assertThat(properties.isPrintStackTrace()).isTrue();
                });
    }

    @Configuration
    static class CustomExceptionHandlerConfiguration {

        @Bean
        FluxExtendedProblemDetailExceptionHandler customFluxExtendedProblemDetailExceptionHandler() {
            return new CustomFluxExtendedProblemDetailExceptionHandler(
                    new ExtendedProblemDetailLog(org.springframework.boot.logging.LogLevel.DEBUG, false));
        }
    }

    @Configuration
    static class CustomLogConfiguration {

        static final ExtendedProblemDetailLog customLog = new ExtendedProblemDetailLog(
                org.springframework.boot.logging.LogLevel.INFO, true);

        @Bean
        ExtendedProblemDetailLog customExtendedProblemDetailLog() {
            return customLog;
        }
    }

    static class CustomFluxExtendedProblemDetailExceptionHandler extends FluxExtendedProblemDetailExceptionHandler {

        CustomFluxExtendedProblemDetailExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
            super(extendedProblemDetailLog);
        }
    }
}

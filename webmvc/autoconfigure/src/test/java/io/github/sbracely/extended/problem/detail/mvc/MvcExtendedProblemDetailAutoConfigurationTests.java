package io.github.sbracely.extended.problem.detail.mvc;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.mvc.handler.MvcExtendedProblemDetailExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MvcExtendedProblemDetailAutoConfiguration}.
 *
 * @since 1.0.0
 */
class MvcExtendedProblemDetailAutoConfigurationTests {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MvcExtendedProblemDetailAutoConfiguration.class));

    @Test
    void shouldAutoConfigureBeans() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MvcExtendedProblemDetailProperties.class);
            assertThat(context).hasSingleBean(ExtendedProblemDetailLog.class);
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
                    assertThat(context).doesNotHaveBean(MvcExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureExceptionHandlerWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(CustomExceptionHandlerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(MvcExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(MvcExtendedProblemDetailExceptionHandler.class))
                            .isInstanceOf(CustomMvcExtendedProblemDetailExceptionHandler.class);
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
                    MvcExtendedProblemDetailProperties properties = context.getBean(MvcExtendedProblemDetailProperties.class);
                    assertThat(properties.getLogLevel().name()).isEqualTo("WARN");
                    assertThat(properties.isPrintStackTrace()).isTrue();
                });
    }

    @Configuration
    static class CustomExceptionHandlerConfiguration {

        @Bean
        MvcExtendedProblemDetailExceptionHandler customMvcExtendedProblemDetailExceptionHandler() {
            return new CustomMvcExtendedProblemDetailExceptionHandler(
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

    static class CustomMvcExtendedProblemDetailExceptionHandler extends MvcExtendedProblemDetailExceptionHandler {

        CustomMvcExtendedProblemDetailExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
            super(extendedProblemDetailLog);
        }
    }
}

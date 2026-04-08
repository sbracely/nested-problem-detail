package io.github.sbracely.extended.problem.detail.mvc;

import io.github.sbracely.extended.problem.detail.common.logging.Boot4CommonExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.mvc.handler.Boot4MvcExtendedProblemDetailExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Boot4MvcExtendedProblemDetailAutoConfiguration}.
 *
 * @since 1.0.0
 */
class Boot4MvcExtendedProblemDetailAutoConfigurationTests {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Boot4MvcExtendedProblemDetailAutoConfiguration.class));

    @Test
    void shouldAutoConfigureBeans() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(Boot4MvcExtendedProblemDetailProperties.class);
            assertThat(context).hasSingleBean(Boot4CommonExtendedProblemDetailLog.class);
            assertThat(context).hasSingleBean(Boot4MvcExtendedProblemDetailExceptionHandler.class);
        });
    }

    @Test
    void shouldNotConfigureWhenDisabled() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(Boot4MvcExtendedProblemDetailProperties.class);
                    assertThat(context).doesNotHaveBean(Boot4CommonExtendedProblemDetailLog.class);
                    assertThat(context).doesNotHaveBean(Boot4MvcExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureExceptionHandlerWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(Boot4MvcCustomExceptionHandlerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(Boot4MvcExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(Boot4MvcExtendedProblemDetailExceptionHandler.class))
                            .isInstanceOf(Boot4MvcCustomMvcExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureLogWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(Boot4MvcCustomLogConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(Boot4CommonExtendedProblemDetailLog.class);
                    assertThat(context.getBean(Boot4CommonExtendedProblemDetailLog.class))
                            .isSameAs(Boot4MvcCustomLogConfiguration.customLog);
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
                    Boot4MvcExtendedProblemDetailProperties properties = context.getBean(Boot4MvcExtendedProblemDetailProperties.class);
                    assertThat(properties.getLogging().getAtLevel().name()).isEqualTo("WARN");
                    assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
                });
    }

    @Configuration
    static class Boot4MvcCustomExceptionHandlerConfiguration {

        @Bean
        Boot4MvcExtendedProblemDetailExceptionHandler customMvcExtendedProblemDetailExceptionHandler() {
            return new Boot4MvcCustomMvcExtendedProblemDetailExceptionHandler(
                    new Boot4CommonExtendedProblemDetailLog(org.springframework.boot.logging.LogLevel.DEBUG, false));
        }
    }

    @Configuration
    static class Boot4MvcCustomLogConfiguration {

        static final Boot4CommonExtendedProblemDetailLog customLog = new Boot4CommonExtendedProblemDetailLog(
                org.springframework.boot.logging.LogLevel.INFO, true);

        @Bean
        Boot4CommonExtendedProblemDetailLog customExtendedProblemDetailLog() {
            return customLog;
        }
    }

    static class Boot4MvcCustomMvcExtendedProblemDetailExceptionHandler extends Boot4MvcExtendedProblemDetailExceptionHandler {

        Boot4MvcCustomMvcExtendedProblemDetailExceptionHandler(Boot4CommonExtendedProblemDetailLog extendedProblemDetailLog) {
            super(extendedProblemDetailLog);
        }
    }
}

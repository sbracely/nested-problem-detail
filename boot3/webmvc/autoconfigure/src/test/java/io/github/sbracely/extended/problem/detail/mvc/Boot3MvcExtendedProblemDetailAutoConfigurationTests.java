package io.github.sbracely.extended.problem.detail.mvc;

import io.github.sbracely.extended.problem.detail.common.logging.Boot3CommonExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.mvc.handler.Boot3MvcExtendedProblemDetailExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Boot3MvcExtendedProblemDetailAutoConfiguration}.
 *
 * @since 1.0.0
 */
class Boot3MvcExtendedProblemDetailAutoConfigurationTests {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Boot3MvcExtendedProblemDetailAutoConfiguration.class));

    @Test
    void shouldAutoConfigureBeans() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(Boot3MvcExtendedProblemDetailProperties.class);
            assertThat(context).hasSingleBean(Boot3CommonExtendedProblemDetailLog.class);
            assertThat(context).hasSingleBean(Boot3MvcExtendedProblemDetailExceptionHandler.class);
        });
    }

    @Test
    void shouldNotConfigureWhenDisabled() {
        this.contextRunner
                .withPropertyValues("extended.problem-detail.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(Boot3MvcExtendedProblemDetailProperties.class);
                    assertThat(context).doesNotHaveBean(Boot3CommonExtendedProblemDetailLog.class);
                    assertThat(context).doesNotHaveBean(Boot3MvcExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureExceptionHandlerWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(Boot3MvcCustomExceptionHandlerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(Boot3MvcExtendedProblemDetailExceptionHandler.class);
                    assertThat(context.getBean(Boot3MvcExtendedProblemDetailExceptionHandler.class))
                            .isInstanceOf(Boot3MvcCustomMvcExtendedProblemDetailExceptionHandler.class);
                });
    }

    @Test
    void shouldNotConfigureLogWhenBeanAlreadyExists() {
        this.contextRunner
                .withUserConfiguration(Boot3MvcCustomLogConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(Boot3CommonExtendedProblemDetailLog.class);
                    assertThat(context.getBean(Boot3CommonExtendedProblemDetailLog.class))
                            .isSameAs(Boot3MvcCustomLogConfiguration.customLog);
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
                    Boot3MvcExtendedProblemDetailProperties properties = context.getBean(Boot3MvcExtendedProblemDetailProperties.class);
                    assertThat(properties.getLogging().getAtLevel().name()).isEqualTo("WARN");
                    assertThat(properties.getLogging().isPrintStackTrace()).isTrue();
                });
    }

    @Configuration
    static class Boot3MvcCustomExceptionHandlerConfiguration {

        @Bean
        Boot3MvcExtendedProblemDetailExceptionHandler customMvcExtendedProblemDetailExceptionHandler() {
            return new Boot3MvcCustomMvcExtendedProblemDetailExceptionHandler(
                    new Boot3CommonExtendedProblemDetailLog(org.springframework.boot.logging.LogLevel.DEBUG, false));
        }
    }

    @Configuration
    static class Boot3MvcCustomLogConfiguration {

        static final Boot3CommonExtendedProblemDetailLog customLog = new Boot3CommonExtendedProblemDetailLog(
                org.springframework.boot.logging.LogLevel.INFO, true);

        @Bean
        Boot3CommonExtendedProblemDetailLog customExtendedProblemDetailLog() {
            return customLog;
        }
    }

    static class Boot3MvcCustomMvcExtendedProblemDetailExceptionHandler extends Boot3MvcExtendedProblemDetailExceptionHandler {

        Boot3MvcCustomMvcExtendedProblemDetailExceptionHandler(Boot3CommonExtendedProblemDetailLog extendedProblemDetailLog) {
            super(extendedProblemDetailLog);
        }
    }
}

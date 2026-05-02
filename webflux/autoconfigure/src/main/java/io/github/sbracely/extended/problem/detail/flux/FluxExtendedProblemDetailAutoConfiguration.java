package io.github.sbracely.extended.problem.detail.flux;

import io.github.sbracely.extended.problem.detail.common.condition.LoggingEnabledCondition;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailStartupLogger;
import io.github.sbracely.extended.problem.detail.flux.advice.FluxExtendedProblemDetailExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;

/**
 * Spring WebFlux Extended Problem Detail Auto Configuration Class.
 * <p>
 * This configuration class is used to automatically configure {@link FluxExtendedProblemDetailExceptionHandler}
 * in Spring WebFlux applications, providing enhanced parameter validation exception handling functionality.
 * When a web application environment is detected and the extended problem detail feature is enabled,
 * it will automatically register the exception handler bean.
 * </p>
 * <p>
 * Configuration conditions:
 * </p>
 * <ul>
 *     <li>Must run in a web application environment ({@link ConditionalOnWebApplication})</li>
 *     <li>Configuration property {@code extended.problem-detail.enabled=true} or not configured (defaults to true)</li>
 *     <li>No custom {@link FluxExtendedProblemDetailExceptionHandler} bean exists in the container</li>
 * </ul>
 * <p>
 * Usage example:
 * </p>
 * <pre>{@code
 * application.yml:
 * extended:
 *   problem-detail:
 *     enabled: true
 * }</pre>
 *
 * @see FluxExtendedProblemDetailExceptionHandler
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(FluxExtendedProblemDetailProperties.class)
@ConditionalOnProperty(prefix = "extended.problem-detail", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FluxExtendedProblemDetailAutoConfiguration {

    /**
     * Creates a new instance of {@link FluxExtendedProblemDetailAutoConfiguration}.
     */
    public FluxExtendedProblemDetailAutoConfiguration() {
    }

    /**
     * Creates and registers the {@link ExtendedProblemDetailLog} bean.
     *
     * @param properties the WebFlux Extended Problem Detail configuration properties
     * @return ExtendedProblemDetailLog instance
     */
    @Bean
    @Conditional(LoggingEnabledCondition.class)
    @ConditionalOnMissingBean
    public ExtendedProblemDetailLog extendedProblemDetailLog(FluxExtendedProblemDetailProperties properties) {
        return new ExtendedProblemDetailLog(properties.getLogging().getAtLevel(), properties.getLogging().isPrintStackTrace());
    }

    /**
     * Logs a startup reminder when the feature is enabled through the default value.
     *
     * @param environment the Spring environment
     * @return the startup logger
     */
    @Bean
    @ConditionalOnMissingBean
    public ExtendedProblemDetailStartupLogger extendedProblemDetailStartupLogger(Environment environment) {
        return new ExtendedProblemDetailStartupLogger(environment, "Spring WebFlux");
    }

    /**
     * Creates and registers the {@link FluxExtendedProblemDetailExceptionHandler} bean.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     * @param properties               the WebFlux Extended Problem Detail configuration properties
     * @return WebFlux Extended Problem Detail Exception Handler instance
     */
    @Bean
    @ConditionalOnMissingBean
    public FluxExtendedProblemDetailExceptionHandler requestExceptionHandler(
            ObjectProvider<ExtendedProblemDetailLog> extendedProblemDetailLog,
            FluxExtendedProblemDetailProperties properties) {
        return new FluxExtendedProblemDetailExceptionHandler(
                extendedProblemDetailLog.getIfAvailable(),
                properties.getErrorsPropertyName(),
                properties.getControllerAdviceOrder());
    }

}

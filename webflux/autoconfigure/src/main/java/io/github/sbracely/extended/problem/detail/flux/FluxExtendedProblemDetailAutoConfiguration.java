package io.github.sbracely.extended.problem.detail.flux;

import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.flux.handler.FluxExtendedProblemDetailExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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
    @ConditionalOnMissingBean
    public ExtendedProblemDetailLog extendedProblemDetailLog(FluxExtendedProblemDetailProperties properties) {
        return new ExtendedProblemDetailLog(properties.getLogging().getAtLevel(), properties.getLogging().isPrintStackTrace());
    }

    /**
     * Creates and registers the {@link FluxExtendedProblemDetailExceptionHandler} bean.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     * @return WebFlux Extended Problem Detail Exception Handler instance
     */
    @Bean
    @ConditionalOnMissingBean
    public FluxExtendedProblemDetailExceptionHandler requestExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
        return new FluxExtendedProblemDetailExceptionHandler(extendedProblemDetailLog);
    }

}

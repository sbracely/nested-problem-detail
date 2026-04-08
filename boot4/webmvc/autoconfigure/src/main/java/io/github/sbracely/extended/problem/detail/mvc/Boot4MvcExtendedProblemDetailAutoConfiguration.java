package io.github.sbracely.extended.problem.detail.mvc;

import io.github.sbracely.extended.problem.detail.common.logging.Boot4CommonExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.mvc.handler.Boot4MvcExtendedProblemDetailExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring WebMVC Extended Problem Detail Auto Configuration Class.
 * <p>
 * This configuration class is used to automatically configure {@link Boot4MvcExtendedProblemDetailExceptionHandler}
 * in Spring WebMVC applications, providing enhanced parameter validation exception handling functionality.
 * When a web application environment is detected and the extended problem detail feature is enabled,
 * it will automatically register the exception handler bean.
 * </p>
 * <p>
 * Configuration conditions:
 * </p>
 * <ul>
 *     <li>Must run in a web application environment ({@link ConditionalOnWebApplication})</li>
 *     <li>Configuration property {@code extended.problem-detail.enabled=true} or not configured (defaults to true)</li>
 *     <li>No custom {@link Boot4MvcExtendedProblemDetailExceptionHandler} bean exists in the container</li>
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
 * @see Boot4MvcExtendedProblemDetailExceptionHandler
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(Boot4MvcExtendedProblemDetailProperties.class)
@ConditionalOnProperty(prefix = "extended.problem-detail", name = "enabled", havingValue = "true", matchIfMissing = true)
public class Boot4MvcExtendedProblemDetailAutoConfiguration {

    /**
     * Creates a new instance of {@link Boot4MvcExtendedProblemDetailAutoConfiguration}.
     */
    public Boot4MvcExtendedProblemDetailAutoConfiguration() {
    }

    /**
     * Creates and registers the {@link Boot4CommonExtendedProblemDetailLog} bean.
     *
     * @param properties the WebMVC Extended Problem Detail configuration properties
     * @return Boot4CommonExtendedProblemDetailLog instance
     */
    @Bean
    @ConditionalOnMissingBean
    public Boot4CommonExtendedProblemDetailLog extendedProblemDetailLog(Boot4MvcExtendedProblemDetailProperties properties) {
        return new Boot4CommonExtendedProblemDetailLog(properties.getLogging().getAtLevel(), properties.getLogging().isPrintStackTrace());
    }

    /**
     * Creates and registers the {@link Boot4MvcExtendedProblemDetailExceptionHandler} bean.
     *
     * @param extendedProblemDetailLog the Boot4CommonExtendedProblemDetailLog instance
     * @return MVC Extended Problem Detail Exception Handler instance
     */
    @Bean
    @ConditionalOnMissingBean
    public Boot4MvcExtendedProblemDetailExceptionHandler requestExceptionHandler(Boot4CommonExtendedProblemDetailLog extendedProblemDetailLog) {
        return new Boot4MvcExtendedProblemDetailExceptionHandler(extendedProblemDetailLog);
    }

}

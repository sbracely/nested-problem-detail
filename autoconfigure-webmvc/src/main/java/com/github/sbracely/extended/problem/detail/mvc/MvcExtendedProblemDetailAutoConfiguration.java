package com.github.sbracely.extended.problem.detail.mvc;

import com.github.sbracely.extended.problem.detail.mvc.handler.MvcExtendedProblemDetailExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring WebMVC Extended Problem Detail Auto Configuration Class.
 * <p>
 * This configuration class is used to automatically configure {@link MvcExtendedProblemDetailExceptionHandler}
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
 *     <li>No custom {@link MvcExtendedProblemDetailExceptionHandler} bean exists in the container</li>
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
 * @see MvcExtendedProblemDetailExceptionHandler
 * @see FluxExtendedProblemDetailAutoConfiguration WebFlux version of auto-configuration
 * @since 0.0.1-SNAPSHOT
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(MvcExtendedProblemDetailProperties.class)
@ConditionalOnProperty(prefix = "extended.problem-detail", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MvcExtendedProblemDetailAutoConfiguration {

    /**
     * Creates and registers WebMVC Extended Problem Detail Exception Handler Bean.
     * <p>
     * This handler inherits from {@link org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler},
     * specifically designed to handle the following types of exceptions:
     * </p>
     * <ul>
     *     <li>{@link org.springframework.web.bind.MethodArgumentNotValidException} - @Valid annotation parameter validation failure</li>
     *     <li>{@link org.springframework.web.method.annotation.HandlerMethodValidationException} - Controller method parameter validation failure</li>
     *     <li>{@link org.springframework.web.bind.support.WebExchangeBindException} - Data binding exception</li>
     * </ul>
     * <p>
     * The handler encapsulates validation error information into {@link com.github.sbracely.extended.problem.detail.core.ExtendedProblemDetail}
     * and returns it, including detailed field-level error information.
     * </p>
     *
     * @return WebMVC Extended Problem Detail Exception Handler instance
     * @see MvcExtendedProblemDetailExceptionHandler
     * @see com.github.sbracely.extended.problem.detail.core.ExtendedProblemDetail
     */
    @Bean
    @ConditionalOnMissingBean
    public MvcExtendedProblemDetailExceptionHandler requestExceptionHandler() {
        return new MvcExtendedProblemDetailExceptionHandler();
    }

}

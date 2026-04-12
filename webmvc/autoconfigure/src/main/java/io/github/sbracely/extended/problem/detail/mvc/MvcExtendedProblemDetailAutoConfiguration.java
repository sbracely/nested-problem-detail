package io.github.sbracely.extended.problem.detail.mvc;

import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetailJacksonSerializer;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.common.response.ProblemDetailFieldVisibility;
import io.github.sbracely.extended.problem.detail.common.response.ProblemDetailJacksonSerializer;
import io.github.sbracely.extended.problem.detail.mvc.advice.MvcExtendedProblemDetailExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleModule;

import java.util.List;

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
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(MvcExtendedProblemDetailProperties.class)
@ConditionalOnProperty(prefix = "extended.problem-detail", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MvcExtendedProblemDetailAutoConfiguration {

    /**
     * Creates a new instance of {@link MvcExtendedProblemDetailAutoConfiguration}.
     */
    public MvcExtendedProblemDetailAutoConfiguration() {
    }

    /**
     * Creates and registers the {@link ExtendedProblemDetailLog} bean.
     *
     * @param properties the WebMVC Extended Problem Detail configuration properties
     * @return ExtendedProblemDetailLog instance
     */
    @Bean
    @ConditionalOnMissingBean
    public ExtendedProblemDetailLog extendedProblemDetailLog(MvcExtendedProblemDetailProperties properties) {
        return new ExtendedProblemDetailLog(properties.getLogging().getAtLevel(), properties.getLogging().isPrintStackTrace());
    }

    /**
     * Creates the effective field visibility rules for ProblemDetail serialization.
     *
     * @param properties  the WebMVC Extended Problem Detail configuration properties
     * @param environment the Spring environment
     * @return the effective field visibility rules
     */
    @Bean
    @ConditionalOnMissingBean
    public ProblemDetailFieldVisibility problemDetailFieldVisibility(
            MvcExtendedProblemDetailProperties properties,
            Environment environment) {
        return ProblemDetailFieldVisibility.from(properties.getField(), List.of(environment.getActiveProfiles()));
    }

    /**
     * Registers the ProblemDetail serializers that apply field visibility rules.
     *
     * @param fieldVisibility the effective field visibility rules
     * @return the object mapper builder customizer
     */
    @Bean
    public JacksonModule extendedProblemDetailJacksonModule(
            ProblemDetailFieldVisibility fieldVisibility) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ProblemDetailJacksonSerializer(fieldVisibility));
        module.addSerializer(new ExtendedProblemDetailJacksonSerializer(fieldVisibility));
        return module;
    }

    /**
     * Creates and registers the {@link MvcExtendedProblemDetailExceptionHandler} bean.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     * @return MVC Extended Problem Detail Exception Handler instance
     */
    @Bean
    @ConditionalOnMissingBean
    public MvcExtendedProblemDetailExceptionHandler requestExceptionHandler(ExtendedProblemDetailLog extendedProblemDetailLog) {
        return new MvcExtendedProblemDetailExceptionHandler(extendedProblemDetailLog);
    }

}

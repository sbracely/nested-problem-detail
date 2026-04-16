package io.github.sbracely.extended.problem.detail.flux;

import io.github.sbracely.extended.problem.detail.common.condition.FieldHideConfiguredCondition;
import io.github.sbracely.extended.problem.detail.common.condition.LoggingEnabledCondition;
import io.github.sbracely.extended.problem.detail.common.field.hide.ExtendedProblemDetailJacksonSerializer;
import io.github.sbracely.extended.problem.detail.common.field.hide.ProblemDetailFieldVisibility;
import io.github.sbracely.extended.problem.detail.common.field.hide.ProblemDetailJacksonSerializer;
import io.github.sbracely.extended.problem.detail.common.logging.ExtendedProblemDetailLog;
import io.github.sbracely.extended.problem.detail.flux.advice.FluxExtendedProblemDetailExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleModule;

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
     * Creates the effective field visibility rules for ProblemDetail serialization.
     *
     * @param properties  the WebFlux Extended Problem Detail configuration properties
     * @param environment the Spring environment
     * @return the effective field visibility rules
     */
    @Bean
    @Conditional(FieldHideConfiguredCondition.class)
    @ConditionalOnMissingBean
    public ProblemDetailFieldVisibility problemDetailFieldVisibility(
            FluxExtendedProblemDetailProperties properties,
            Environment environment) {
        return ProblemDetailFieldVisibility.from(properties.getField(), java.util.List.of(environment.getActiveProfiles()));
    }

    /**
     * Registers the ProblemDetail serializers that apply field visibility rules.
     *
     * @param fieldVisibility the effective field visibility rules
     * @return the object mapper builder customizer
     */
    @Bean
    @ConditionalOnBean(ProblemDetailFieldVisibility.class)
    public JacksonModule extendedProblemDetailJacksonModule(
            ProblemDetailFieldVisibility fieldVisibility) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ProblemDetailJacksonSerializer(fieldVisibility));
        module.addSerializer(new ExtendedProblemDetailJacksonSerializer(fieldVisibility));
        return module;
    }

    /**
     * Creates and registers the {@link FluxExtendedProblemDetailExceptionHandler} bean.
     *
     * @param extendedProblemDetailLog the ExtendedProblemDetailLog instance
     * @return WebFlux Extended Problem Detail Exception Handler instance
     */
    @Bean
    @ConditionalOnMissingBean
    public FluxExtendedProblemDetailExceptionHandler requestExceptionHandler(
            ObjectProvider<ExtendedProblemDetailLog> extendedProblemDetailLog) {
        return new FluxExtendedProblemDetailExceptionHandler(extendedProblemDetailLog.getIfAvailable());
    }

}

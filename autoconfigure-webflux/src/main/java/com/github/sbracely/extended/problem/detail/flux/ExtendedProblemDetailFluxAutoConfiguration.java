package com.github.sbracely.extended.problem.detail.flux;

import com.github.sbracely.extended.problem.detail.flux.handler.ExtendedProblemDetailFluxExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Autoconfiguration for extended ProblemDetail exception handling.
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(ExtendedProblemDetailFluxProperties.class)
@ConditionalOnProperty(prefix = "extended.problem-detail", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExtendedProblemDetailFluxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExtendedProblemDetailFluxExceptionHandler requestExceptionHandler() {
        return new ExtendedProblemDetailFluxExceptionHandler();
    }

}

package com.github.sbracely.nested.problem.detail;

import com.github.sbracely.nested.problem.detail.handler.RequestExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Autoconfiguration for nested ProblemDetail exception handling.
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(NestedProblemDetailProperties.class)
@ConditionalOnProperty(prefix = "nested.problem-detail", name = "enabled", havingValue = "true", matchIfMissing = true)
public class NestedProblemDetailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RequestExceptionHandler requestExceptionHandler(NestedProblemDetailProperties properties) {
        return new RequestExceptionHandler(properties);
    }

}

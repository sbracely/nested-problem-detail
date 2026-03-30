package com.github.sbracely.extended.problem.detail.mvc;

import com.github.sbracely.extended.problem.detail.mvc.handler.MvcExtendedProblemDetailExceptionHandler;
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
@EnableConfigurationProperties(ExtendedProblemDetailMvcProperties.class)
@ConditionalOnProperty(prefix = "extended.problem-detail", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExtendedProblemDetailMvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MvcExtendedProblemDetailExceptionHandler requestExceptionHandler() {
        return new MvcExtendedProblemDetailExceptionHandler();
    }

}

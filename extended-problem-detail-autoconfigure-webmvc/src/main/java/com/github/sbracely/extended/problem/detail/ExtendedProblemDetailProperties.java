package com.github.sbracely.extended.problem.detail;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for extended ProblemDetail exception handling.
 */
@ConfigurationProperties(prefix = "extended.problem-detail")
public class ExtendedProblemDetailProperties {

    /**
     * Whether to enable extended ProblemDetail exception handling.
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}

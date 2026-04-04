package com.github.sbracely.extended.problem.detail.core.properties;

import org.springframework.boot.logging.LogLevel;

/**
 * Extended Problem Detail Configuration Properties Base Class.
 * <p>
 * This class defines common configuration properties for the extended problem detail feature,
 * used by both Spring WebMVC and Spring WebFlux auto-configurations.
 * </p>
 * <p>
 * Configuration properties:
 * </p>
 * <ul>
 *     <li>{@code enabled} - Whether to enable the extended problem detail feature, defaults to {@code true}</li>
 *     <li>{@code log-level} - Log level for validation exceptions, defaults to {@code DEBUG}</li>
 *     <li>{@code print-stack-trace} - Whether to print exception stack trace in logs, defaults to {@code false}</li>
 * </ul>
 * <p>
 * Usage example:
 * </p>
 * <pre>{@code
 * # application.yml
 * extended:
 *   problem-detail:
 *     enabled: true
 *     log-level: DEBUG  # TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
 *     print-stack-trace: false
 * }</pre>
 * <pre>{@code
 * # application.properties
 * extended.problem-detail.enabled=true
 * extended.problem-detail.log-level=DEBUG
 * extended.problem-detail.print-stack-trace=false
 * }</pre>
 *
 * @see LogLevel
 * @since 0.0.1-SNAPSHOT
 */
public class ExtendedProblemDetailProperties {

    /**
     * Extended problem detail feature enabled status.
     * <p>
     * When set to {@code true}, enables extended problem detail exception handling functionality;
     * when set to {@code false}, disables this feature and uses Spring's default error handling mechanism.
     * </p>
     */
    private boolean enabled = true;

    /**
     * Log level for validation exception handling.
     * <p>
     * Determines the log level used when logging validation exceptions.
     * Default is {@link LogLevel#DEBUG} as validation failures are normal business scenarios.
     * Set to {@link LogLevel#OFF} to disable logging.
     * </p>
     */
    private LogLevel logLevel = LogLevel.DEBUG;

    /**
     * Whether to print exception stack trace in logs.
     * <p>
     * When set to {@code true}, exception stack traces will be included in log output.
     * Default is {@code false} as stack traces can be verbose and validation failures
     * are typically normal business scenarios that don't require stack trace details.
     * </p>
     */
    private boolean printStackTrace = false;

    /**
     * Gets the enabled status of the extended problem detail feature.
     *
     * @return {@code true} if the feature is enabled, {@code false} if disabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled status of the extended problem detail feature.
     *
     * @param enabled {@code true} to enable the feature, {@code false} to disable it
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the log level for validation exception handling.
     *
     * @return the log level
     */
    public LogLevel getLogLevel() {
        return logLevel;
    }

    /**
     * Sets the log level for validation exception handling.
     *
     * @param logLevel the log level to set
     */
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Gets whether to print exception stack trace in logs.
     *
     * @return {@code true} if stack traces should be printed, {@code false} otherwise
     */
    public boolean isPrintStackTrace() {
        return printStackTrace;
    }

    /**
     * Sets whether to print exception stack trace in logs.
     *
     * @param printStackTrace {@code true} to print stack traces, {@code false} otherwise
     */
    public void setPrintStackTrace(boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
    }

}

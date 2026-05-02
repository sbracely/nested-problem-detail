package io.github.sbracely.extended.problem.detail.common.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
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
 *     <li>{@code errors-property-name} - ProblemDetail properties entry name for structured errors, defaults to {@code errors}</li>
 *     <li>{@code controller-advice-order} - {@code @ControllerAdvice} order used by built-in exception handlers, defaults to {@code 0}</li>
 *     <li>{@code logging.at-level} - Log level at which exceptions are recorded, defaults to {@code INFO}</li>
 *     <li>{@code logging.print-stack-trace} - Whether to print exception stack trace in logs, defaults to {@code false}</li>
 * </ul>
 * <p>
 * Usage example:
 * </p>
 * <pre>{@code
 * # application.yml
 * extended:
 *   problem-detail:
 *     enabled: true
 *     errors-property-name: errors
 *     controller-advice-order: 0
 *     logging:
 *       at-level: DEBUG  # TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
 *       print-stack-trace: false
 * }</pre>
 * <pre>{@code
 * # application.properties
 * extended.problem-detail.enabled=true
 * extended.problem-detail.errors-property-name=errors
 * extended.problem-detail.controller-advice-order=0
 * extended.problem-detail.logging.at-level=INFO
 * extended.problem-detail.logging.print-stack-trace=false
 * }</pre>
 *
 * @see LogLevel
 * @since 1.0.0
 */
public class ExtendedProblemDetailProperties {

    /**
     * Default ProblemDetail properties entry name used for structured errors.
     */
    public static final String DEFAULT_ERRORS_PROPERTY_NAME = "errors";

    /**
     * Default {@code @ControllerAdvice} order for built-in exception handlers.
     */
    public static final int DEFAULT_CONTROLLER_ADVICE_ORDER = 0;

    /**
     * Extended problem detail feature enabled status.
     * <p>
     * When set to {@code true}, enables extended problem detail exception handling functionality;
     * when set to {@code false}, disables this feature and uses Spring's default error handling mechanism.
     * </p>
     */
    private boolean enabled = true;

    /**
     * Name of the {@link org.springframework.http.ProblemDetail} properties entry that contains structured errors.
     */
    private String errorsPropertyName = DEFAULT_ERRORS_PROPERTY_NAME;

    /**
     * {@code @ControllerAdvice} order used by built-in exception handlers.
     */
    private int controllerAdviceOrder = DEFAULT_CONTROLLER_ADVICE_ORDER;

    /**
     * CommonLogging configuration for extended problem detail exception handling.
     */
    @NestedConfigurationProperty
    private CommonLogging logging = new CommonLogging();

    /**
     * Creates a new instance with default property values.
     */
    public ExtendedProblemDetailProperties() {
    }

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
     * Gets the ProblemDetail properties entry name used for structured errors.
     *
     * @return the properties entry name
     */
    public String getErrorsPropertyName() {
        return errorsPropertyName;
    }

    /**
     * Sets the ProblemDetail properties entry name used for structured errors.
     *
     * @param errorsPropertyName the properties entry name to set
     */
    public void setErrorsPropertyName(String errorsPropertyName) {
        this.errorsPropertyName = errorsPropertyName;
    }

    /**
     * Gets the {@code @ControllerAdvice} order used by built-in exception handlers.
     *
     * @return the handler order
     */
    public int getControllerAdviceOrder() {
        return controllerAdviceOrder;
    }

    /**
     * Sets the {@code @ControllerAdvice} order used by built-in exception handlers.
     *
     * @param controllerAdviceOrder the handler order to set
     */
    public void setControllerAdviceOrder(int controllerAdviceOrder) {
        this.controllerAdviceOrder = controllerAdviceOrder;
    }

    /**
     * Gets the logging configuration.
     *
     * @return the logging configuration
     */
    public CommonLogging getLogging() {
        return logging;
    }

    /**
     * Sets the logging configuration.
     *
     * @param logging the logging configuration to set
     */
    public void setLogging(CommonLogging logging) {
        this.logging = logging;
    }

    /**
     * CommonLogging configuration for extended problem detail exception handling.
     */
    public static class CommonLogging {

        /**
         * The log level at which exceptions are recorded.
         * <p>
         * Specifies which level is used to emit the log entry when an exception is caught.
         * Default is {@link LogLevel#INFO} as validation failures are normal business events visible in production.
         * Set to {@link LogLevel#OFF} to disable logging.
         * </p>
         */
        private LogLevel atLevel = LogLevel.INFO;

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
         * Creates a new instance with default logging configuration.
         */
        public CommonLogging() {
        }

        /**
         * Gets the log level at which exceptions are recorded.
         *
         * @return the log level
         */
        public LogLevel getAtLevel() {
            return atLevel;
        }

        /**
         * Sets the log level at which exceptions are recorded.
         *
         * @param atLevel the log level to set
         */
        public void setAtLevel(LogLevel atLevel) {
            this.atLevel = atLevel;
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

}

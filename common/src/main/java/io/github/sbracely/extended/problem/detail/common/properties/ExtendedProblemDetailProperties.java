package io.github.sbracely.extended.problem.detail.common.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.logging.LogLevel;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
 *     <li>{@code logging.at-level} - Log level at which exceptions are recorded, defaults to {@code DEBUG}</li>
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
 *     logging:
 *       at-level: DEBUG  # TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
 *       print-stack-trace: false
 * }</pre>
 * <pre>{@code
 * # application.properties
 * extended.problem-detail.enabled=true
 * extended.problem-detail.logging.at-level=DEBUG
 * extended.problem-detail.logging.print-stack-trace=false
 * }</pre>
 *
 * @see LogLevel
 * @since 1.0.0
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
     * CommonLogging configuration for extended problem detail exception handling.
     */
    @NestedConfigurationProperty
    private CommonLogging logging = new CommonLogging();

    /**
     * Field visibility configuration for ProblemDetail responses.
     */
    @NestedConfigurationProperty
    private FieldVisibility field = new FieldVisibility();

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
     * Gets the field visibility configuration.
     *
     * @return the field visibility configuration
     */
    public FieldVisibility getField() {
        return field;
    }

    /**
     * Sets the field visibility configuration.
     *
     * @param field the field visibility configuration to set
     */
    public void setField(FieldVisibility field) {
        this.field = field;
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

    /**
     * Field visibility configuration shared by both global and profile-specific settings.
     */
    public static class FieldVisibility {
        /**
         * default
         */
        public FieldVisibility() {
        }

        /**
         * Fields that should be hidden from the response.
         */
        private Set<String> hide = new LinkedHashSet<>();

        /**
         * Profile-specific field visibility settings.
         */
        private Map<String, FieldRule> profiles = new LinkedHashMap<>();

        /**
         * Gets the explicitly hidden fields.
         *
         * @return the fields to hide
         */
        public Set<String> getHide() {
            return hide;
        }

        /**
         * Sets the explicitly hidden fields.
         *
         * @param hide the fields to hide
         */
        public void setHide(Set<String> hide) {
            this.hide = hide;
        }

        /**
         * Gets the profile-specific field rules.
         *
         * @return the profile-specific field rules
         */
        public Map<String, FieldRule> getProfiles() {
            return profiles;
        }

        /**
         * Sets the profile-specific field rules.
         *
         * @param profiles the profile-specific field rules
         */
        public void setProfiles(Map<String, FieldRule> profiles) {
            this.profiles = profiles;
        }
    }

    /**
     * Profile-specific field visibility configuration.
     */
    public static class FieldRule {
        /**
         * default
         */
        public FieldRule() {
        }

        /**
         * Fields that should be hidden for the profile.
         */
        private Set<String> hide = new LinkedHashSet<>();

        /**
         * Gets the explicitly hidden fields.
         *
         * @return the fields to hide
         */
        public Set<String> getHide() {
            return hide;
        }

        /**
         * Sets the explicitly hidden fields.
         *
         * @param hide the fields to hide
         */
        public void setHide(Set<String> hide) {
            this.hide = hide;
        }
    }

}

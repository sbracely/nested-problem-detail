package io.github.sbracely.extended.problem.detail.flux;

import io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring WebFlux Extended Problem Detail Configuration Properties Class.
 * <p>
 * This class is used to bind configuration properties with the prefix {@code extended.problem-detail}
 * from {@code application.yml} or {@code application.properties}, providing configuration options
 * for the extended problem detail feature.
 * </p>
 * <p>
 * Configuration properties:
 * </p>
 * <ul>
 *     <li>{@code enabled} - Whether to enable the extended problem detail feature, defaults to {@code true}</li>
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
 *     logging:
 *       at-level: DEBUG  # TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
 *       print-stack-trace: false
 * }</pre>
 * <pre>{@code
 * # application.properties
 * extended.problem-detail.enabled=true
 * extended.problem-detail.logging.at-level=INFO
 * extended.problem-detail.logging.print-stack-trace=false
 * }</pre>
 *
 * @see ConfigurationProperties
 * @see ExtendedProblemDetailProperties
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "extended.problem-detail")
public class FluxExtendedProblemDetailProperties extends ExtendedProblemDetailProperties {

    /**
     * Creates a new instance with default property values.
     */
    public FluxExtendedProblemDetailProperties() {
    }

}

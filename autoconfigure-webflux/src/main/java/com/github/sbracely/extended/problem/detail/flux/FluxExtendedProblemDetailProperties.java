package com.github.sbracely.extended.problem.detail.flux;

import com.github.sbracely.extended.problem.detail.core.properties.ExtendedProblemDetailProperties;
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
 * @see ConfigurationProperties
 * @see ExtendedProblemDetailProperties
 * @since 0.0.1-SNAPSHOT
 */
@ConfigurationProperties(prefix = "extended.problem-detail")
public class FluxExtendedProblemDetailProperties extends ExtendedProblemDetailProperties {

}

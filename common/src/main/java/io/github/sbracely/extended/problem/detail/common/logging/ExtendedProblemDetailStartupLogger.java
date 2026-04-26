package io.github.sbracely.extended.problem.detail.common.logging;

import io.github.sbracely.extended.problem.detail.common.properties.ExtendedProblemDetailProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Logs a one-time startup reminder when extended problem detail is enabled by default.
 *
 * @since 1.1.0
 */
public final class ExtendedProblemDetailStartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    static final String ENABLED_PROPERTY = "extended.problem-detail.enabled";

    static final String LOGGING_PROPERTY = "extended.problem-detail.logging";

    static final String LOGGING_AT_LEVEL_PROPERTY = "extended.problem-detail.logging.at-level";

    static final String LOGGING_PRINT_STACK_TRACE_PROPERTY = "extended.problem-detail.logging.print-stack-trace";

    private final Log logger = LogFactory.getLog(getClass());

    private final Environment environment;

    private final String stackName;

    private final AtomicBoolean logged = new AtomicBoolean();

    /**
     * Creates a startup logger for the given web stack.
     *
     * @param environment the application environment
     * @param stackName   the web stack name shown in the log message
     */
    public ExtendedProblemDetailStartupLogger(Environment environment, String stackName) {
        this.environment = environment;
        this.stackName = stackName;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!this.logged.compareAndSet(false, true)) {
            return;
        }
        if (Binder.get(this.environment).bind(ENABLED_PROPERTY, Boolean.class).isBound()) {
            return;
        }
        if (this.logger.isInfoEnabled()) {
            ExtendedProblemDetailProperties.CommonLogging logging = Binder.get(this.environment)
                    .bind(LOGGING_PROPERTY, ExtendedProblemDetailProperties.CommonLogging.class)
                    .orElseGet(ExtendedProblemDetailProperties.CommonLogging::new);
            this.logger.info("Extended Problem Detail is enabled by default for " + this.stackName
                    + ". Logging: " + LOGGING_AT_LEVEL_PROPERTY + "=" + logging.getAtLevel() + ", "
                    + LOGGING_PRINT_STACK_TRACE_PROPERTY + "=" + logging.isPrintStackTrace()
                    + ". To disable it, set '"
                    + ENABLED_PROPERTY + "=false'");
        }
    }

}

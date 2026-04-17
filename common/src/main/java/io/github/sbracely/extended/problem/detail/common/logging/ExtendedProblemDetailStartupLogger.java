package io.github.sbracely.extended.problem.detail.common.logging;

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
            this.logger.info("Extended Problem Detail is enabled by default for " + this.stackName
                    + ". To disable it, set '" + ENABLED_PROPERTY + "=false'");
        }
    }

}

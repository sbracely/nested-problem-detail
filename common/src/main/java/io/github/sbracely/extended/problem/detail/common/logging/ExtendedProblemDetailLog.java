package io.github.sbracely.extended.problem.detail.common.logging;

import org.apache.commons.logging.Log;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.logging.LogLevel;

/**
 * Logger for extended problem detail with configurable log level and stack trace printing.
 * <p>
 * This class provides a convenient way to log messages at a dynamically
 * configured log level.
 * </p>
 *
 * @since 1.0.0
 */
public final class ExtendedProblemDetailLog {

    private final LogLevel logLevel;
    private final boolean printStackTrace;

    /**
     * Creates a new instance with the specified configuration.
     *
     * @param logLevel        the log level
     * @param printStackTrace whether to print exception stack trace
     */
    public ExtendedProblemDetailLog(LogLevel logLevel, boolean printStackTrace) {
        this.logLevel = logLevel;
        this.printStackTrace = printStackTrace;
    }

    /**
     * Logs a message at the configured level.
     *
     * @param logger  the logger to use
     * @param message the message to log
     */
    public void log(Log logger, String message) {
        log(logger, null, message);
    }

    /**
     * Logs a message at the configured level with an optional exception.
     *
     * @param logger  the logger to use
     * @param ex      the exception to log (can be null)
     * @param message the message to log
     */
    public void log(Log logger, @Nullable Throwable ex, String message) {
        if (logLevel == LogLevel.OFF) {
            return;
        }

        boolean shouldPrintStackTrace = ex != null && this.printStackTrace;

        if (shouldPrintStackTrace) {
            logLevel.log(logger, message, ex);
        } else {
            logLevel.log(logger, message);
        }
    }
}

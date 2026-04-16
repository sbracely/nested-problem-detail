package io.github.sbracely.extended.problem.detail.common.logging;

import org.apache.commons.logging.Log;
import org.jspecify.annotations.Nullable;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.boot.logging.LogLevel;

/**
 * Logger for extended problem detail with configurable log level and stack trace printing.
 * <p>
 * This class provides a convenient way to log messages at a dynamically
 * configured log level, supporting SLF4J-style placeholder syntax.
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
     * Logs a message with placeholder support.
     * <p>
     * Supports SLF4J-style placeholders: {@code log(logger, "Error processing {}", name)}
     * </p>
     *
     * @param logger  the logger to use
     * @param message the message with optional placeholders
     * @param args    the arguments to replace placeholders
     */
    public void log(Log logger, String message, @Nullable Object... args) {
        log(logger, null, message, args);
    }

    /**
     * Logs a message with placeholder support and optional exception.
     * <p>
     * Supports SLF4J-style placeholders: {@code log(logger, ex, "Error processing {}", name)}
     * </p>
     *
     * @param logger  the logger to use
     * @param ex      the exception to log (can be null)
     * @param message the message with optional placeholders
     * @param args    the arguments to replace placeholders
     */
    public void log(Log logger,
                    @Nullable Throwable ex,
                    String message,
                    @Nullable Object... args) {
        if (logLevel == LogLevel.OFF) {
            return;
        }

        String formattedMessage = MessageFormatter.arrayFormat(message, args).getMessage();
        boolean shouldPrintStackTrace = ex != null && this.printStackTrace;

        if (shouldPrintStackTrace) {
            logLevel.log(logger, formattedMessage, ex);
        } else {
            logLevel.log(logger, formattedMessage);
        }
    }
}

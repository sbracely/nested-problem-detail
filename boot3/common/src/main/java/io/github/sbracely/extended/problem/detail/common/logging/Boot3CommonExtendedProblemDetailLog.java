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
public final class Boot3CommonExtendedProblemDetailLog {

    private final LogLevel logLevel;
    private final boolean printStackTrace;

    /**
     * Creates a new instance with the specified configuration.
     *
     * @param logLevel        the log level
     * @param printStackTrace whether to print exception stack trace
     */
    public Boot3CommonExtendedProblemDetailLog(LogLevel logLevel, boolean printStackTrace) {
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
        log(logger, ex, true, false, message, args);
    }

    /**
     * Logs a message with placeholder support and optional exception.
     * <p>
     * Supports SLF4J-style placeholders: {@code log(logger, ex, "Error processing {}", name)}
     * </p>
     *
     * @param logger           the logger to use
     * @param throwable        the exception to log (can be null)
     * @param printStackTrace  whether to print exception stack trace
     * @param printExceptionId whether to print exception identity hash code
     * @param message          the message with optional placeholders
     * @param args             the arguments to replace placeholders
     */
    public void log(Log logger,
                    @Nullable Throwable throwable,
                    boolean printStackTrace,
                    boolean printExceptionId,
                    String message,
                    @Nullable Object... args) {
        if (logLevel == LogLevel.OFF) {
            return;
        }

        String formattedMessage = buildMessage(throwable, printExceptionId, message, args);
        doLog(logger, formattedMessage, throwable, printStackTrace);
    }

    private String buildMessage(@Nullable Throwable throwable,
                                boolean printExceptionId,
                                String message,
                                @Nullable Object... args) {
        String formatted = MessageFormatter.arrayFormat(message, args).getMessage();

        if (!printExceptionId || throwable == null) {
            return formatted;
        }

        return "[exception#" + Integer.toHexString(System.identityHashCode(throwable)) + "] " + formatted;
    }

    private void doLog(Log logger, String message, @Nullable Throwable throwable, boolean printStackTrace) {
        boolean shouldPrintStackTrace = this.printStackTrace && printStackTrace && throwable != null;

        if (shouldPrintStackTrace) {
            logLevel.log(logger, message, throwable);
        } else {
            logLevel.log(logger, message);
        }
    }
}

package io.github.sbracely.extended.problem.detail.common.logging;

import org.apache.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ExtendedProblemDetailLog} class.
 */
class ExtendedProblemDetailLogTest {

    private final CommonRecordingLog logger = new CommonRecordingLog();

    @Test
    void shouldLogStackTraceDebugWith() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, true);
        Exception exception = new RuntimeException("test error");

        log.log(logger, exception, "Error occurred: {}", "details");

        assertThat(logger.events()).containsExactly(new CommonLogEvent("debug", "Error occurred: details", exception));
    }

    @Test
    void shouldLogStackTraceDebugWithout() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, false);
        Exception exception = new RuntimeException("test error");

        log.log(logger, exception, "Error occurred: {}", "details");

        assertThat(logger.events()).containsExactly(new CommonLogEvent("debug", "Error occurred: details", null));
    }

    @Test
    void shouldLogInfoLevel() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.INFO, false);

        log.log(logger, "Info message");

        assertThat(logger.events()).containsExactly(new CommonLogEvent("info", "Info message", null));
    }

    @Test
    void shouldLogWarnLevel() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.WARN, false);

        log.log(logger, "Warning message");

        assertThat(logger.events()).containsExactly(new CommonLogEvent("warn", "Warning message", null));
    }

    @Test
    void shouldLogErrorLevel() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.ERROR, false);
        Exception exception = new RuntimeException("error");

        log.log(logger, exception, "Error message");

        assertThat(logger.events()).containsExactly(new CommonLogEvent("error", "Error message", null));
    }

    @Test
    void shouldNotLogWhenLevelIsOff() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.OFF, true);

        log.log(logger, "This should not be logged");

        assertThat(logger.events()).isEmpty();
    }

    @Test
    void shouldHandleMultiplePlaceholders() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, false);

        log.log(logger, "Error: {} - Code: {} - Status: {}", "validation", 400, "BAD_REQUEST");

        assertThat(logger.events()).containsExactly(
                new CommonLogEvent("debug", "Error: validation - Code: 400 - Status: BAD_REQUEST", null));
    }

    @Test
    void shouldHandleNullExceptionWithStackTraceEnabled() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, true);

        log.log(logger, "Message without exception");

        assertThat(logger.events()).containsExactly(new CommonLogEvent("debug", "Message without exception", null));
    }

    @Test
    void shouldLogLevel() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.TRACE, false);

        log.log(logger, "Trace message");

        assertThat(logger.events()).containsExactly(new CommonLogEvent("trace", "Trace message", null));
    }

    @Test
    void shouldLogFatalLevel() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.FATAL, false);
        Exception exception = new RuntimeException("fatal error");

        log.log(logger, exception, "Fatal error occurred");

        assertThat(logger.events()).containsExactly(new CommonLogEvent("fatal", "Fatal error occurred", null));
    }

    private record CommonLogEvent(String level, String message, Throwable throwable) {
    }

    private static final class CommonRecordingLog implements Log {

        private final List<CommonLogEvent> events = new ArrayList<>();

        List<CommonLogEvent> events() {
            return events;
        }

        private void add(String level, Object message, Throwable throwable) {
            events.add(new CommonLogEvent(level, String.valueOf(message), throwable));
        }

        @Override
        public boolean isDebugEnabled() {
            return true;
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public boolean isFatalEnabled() {
            return true;
        }

        @Override
        public boolean isInfoEnabled() {
            return true;
        }

        @Override
        public boolean isTraceEnabled() {
            return true;
        }

        @Override
        public boolean isWarnEnabled() {
            return true;
        }

        @Override
        public void trace(Object message) {
            add("trace", message, null);
        }

        @Override
        public void trace(Object message, Throwable t) {
            add("trace", message, t);
        }

        @Override
        public void debug(Object message) {
            add("debug", message, null);
        }

        @Override
        public void debug(Object message, Throwable t) {
            add("debug", message, t);
        }

        @Override
        public void info(Object message) {
            add("info", message, null);
        }

        @Override
        public void info(Object message, Throwable t) {
            add("info", message, t);
        }

        @Override
        public void warn(Object message) {
            add("warn", message, null);
        }

        @Override
        public void warn(Object message, Throwable t) {
            add("warn", message, t);
        }

        @Override
        public void error(Object message) {
            add("error", message, null);
        }

        @Override
        public void error(Object message, Throwable t) {
            add("error", message, t);
        }

        @Override
        public void fatal(Object message) {
            add("fatal", message, null);
        }

        @Override
        public void fatal(Object message, Throwable t) {
            add("fatal", message, t);
        }
    }
}

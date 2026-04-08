package io.github.sbracely.extended.problem.detail.common.logging;

import org.apache.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Boot3CommonExtendedProblemDetailLog} class.
 */
class Boot3CommonExtendedProblemDetailLogTest {

    private final Boot3CommonRecordingLog logger = new Boot3CommonRecordingLog();

    @Test
    void shouldLogStackTraceDebugWith() {
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.DEBUG, true);
        Exception exception = new RuntimeException("test error");

        log.log(logger, exception, "Error occurred: {}", "details");

        assertThat(logger.events()).containsExactly(new Boot3CommonLogEvent("debug", "Error occurred: details", exception));
    }

    @Test
    void shouldLogStackTraceDebugWithout() {
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.DEBUG, false);
        Exception exception = new RuntimeException("test error");

        log.log(logger, exception, "Error occurred: {}", "details");

        assertThat(logger.events()).containsExactly(new Boot3CommonLogEvent("debug", "Error occurred: details", null));
    }

    @Test
    void shouldLogInfoLevel() {
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.INFO, false);

        log.log(logger, "Info message");

        assertThat(logger.events()).containsExactly(new Boot3CommonLogEvent("info", "Info message", null));
    }

    @Test
    void shouldLogWarnLevel() {
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.WARN, false);

        log.log(logger, "Warning message");

        assertThat(logger.events()).containsExactly(new Boot3CommonLogEvent("warn", "Warning message", null));
    }

    @Test
    void shouldLogErrorLevel() {
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.ERROR, false);
        Exception exception = new RuntimeException("error");

        log.log(logger, exception, "Error message");

        assertThat(logger.events()).containsExactly(new Boot3CommonLogEvent("error", "Error message", null));
    }

    @Test
    void shouldNotLogWhenLevelIsOff() {
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.OFF, true);

        log.log(logger, "This should not be logged");

        assertThat(logger.events()).isEmpty();
    }

    @Test
    void shouldHandleMultiplePlaceholders() {
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.DEBUG, false);

        log.log(logger, "Error: {} - Code: {} - Status: {}", "validation", 400, "BAD_REQUEST");

        assertThat(logger.events()).containsExactly(
                new Boot3CommonLogEvent("debug", "Error: validation - Code: 400 - Status: BAD_REQUEST", null));
    }

    @Test
    void shouldHandleNullExceptionWithStackTraceEnabled() {
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.DEBUG, true);

        log.log(logger, "Message without exception");

        assertThat(logger.events()).containsExactly(new Boot3CommonLogEvent("debug", "Message without exception", null));
    }

    @Test
    void shouldLogLevel() {
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.TRACE, false);

        log.log(logger, "Trace message");

        assertThat(logger.events()).containsExactly(new Boot3CommonLogEvent("trace", "Trace message", null));
    }

    @Test
    void shouldLogFatalLevel() {
        Boot3CommonExtendedProblemDetailLog log = new Boot3CommonExtendedProblemDetailLog(LogLevel.FATAL, false);
        Exception exception = new RuntimeException("fatal error");

        log.log(logger, exception, "Fatal error occurred");

        assertThat(logger.events()).containsExactly(new Boot3CommonLogEvent("fatal", "Fatal error occurred", null));
    }

    private record Boot3CommonLogEvent(String level, String message, Throwable throwable) {
    }

    private static final class Boot3CommonRecordingLog implements Log {

        private final List<Boot3CommonLogEvent> events = new ArrayList<>();

        List<Boot3CommonLogEvent> events() {
            return events;
        }

        private void add(String level, Object message, Throwable throwable) {
            events.add(new Boot3CommonLogEvent(level, String.valueOf(message), throwable));
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

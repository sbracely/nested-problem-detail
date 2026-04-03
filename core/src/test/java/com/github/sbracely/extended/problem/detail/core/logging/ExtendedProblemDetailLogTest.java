package com.github.sbracely.extended.problem.detail.core.logging;

import org.apache.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.logging.LogLevel;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ExtendedProblemDetailLog} class.
 */
@ExtendWith(MockitoExtension.class)
class ExtendedProblemDetailLogTest {

    @Mock
    private Log logger;

    @Test
    void shouldLogDebugWithStackTrace() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, true);
        Exception exception = new RuntimeException("test error");

        log.log(logger, exception, "Error occurred: {}", "details");

        verify(logger).debug("Error occurred: details", exception);
    }

    @Test
    void shouldLogDebugWithoutStackTrace() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, false);
        Exception exception = new RuntimeException("test error");

        log.log(logger, exception, "Error occurred: {}", "details");

        verify(logger).debug("Error occurred: details", (Throwable) null);
    }

    @Test
    void shouldLogInfoLevel() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.INFO, false);

        log.log(logger, null, "Info message");

        verify(logger).info("Info message", (Throwable) null);
    }

    @Test
    void shouldLogWarnLevel() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.WARN, false);

        log.log(logger, null, "Warning message");

        verify(logger).warn("Warning message", (Throwable) null);
    }

    @Test
    void shouldLogErrorLevel() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.ERROR, false);
        Exception exception = new RuntimeException("error");

        log.log(logger, exception, "Error message");

        verify(logger).error("Error message", (Throwable) null);
    }

    @Test
    void shouldNotLogWhenLevelIsOff() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.OFF, true);

        log.log(logger, null, "This should not be logged");

        verifyNoInteractions(logger);
    }

    @Test
    void shouldHandleMultiplePlaceholders() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, false);

        log.log(logger, null, "Error: {} - Code: {} - Status: {}", "validation", 400, "BAD_REQUEST");

        verify(logger).debug("Error: validation - Code: 400 - Status: BAD_REQUEST", (Throwable) null);
    }

    @Test
    void shouldHandleNullExceptionWithStackTraceEnabled() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.DEBUG, true);

        log.log(logger, null, "Message without exception");

        verify(logger).debug("Message without exception", (Throwable) null);
    }

    @Test
    void shouldLogTraceLevel() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.TRACE, false);

        log.log(logger, null, "Trace message");

        verify(logger).trace("Trace message", (Throwable) null);
    }

    @Test
    void shouldLogFatalLevel() {
        ExtendedProblemDetailLog log = new ExtendedProblemDetailLog(LogLevel.FATAL, false);
        Exception exception = new RuntimeException("fatal error");

        log.log(logger, exception, "Fatal error occurred");

        verify(logger).fatal("Fatal error occurred", (Throwable) null);
    }
}

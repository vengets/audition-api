package com.audition.common.logging;

import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.test.util.ReflectionTestUtils;

class AuditionLoggerTest {

    @SuppressWarnings("PMD.LoggerIsNotStaticFinal")
    @Mock
    private transient Logger logger;

    @InjectMocks
    private transient AuditionLogger auditionLogger;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        auditionLogger = new AuditionLogger();

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        ReflectionTestUtils.setField(auditionLogger, "objectMapper", mapper);
    }

    @Test
    void shouldLogInfoWhenEnabled() {
        when(logger.isInfoEnabled()).thenReturn(true);
        auditionLogger.info(logger, "Test info");
        verify(logger).info("Test info");
    }

    @Test
    void shouldLogInfoWithObjectWhenEnabled() {
        when(logger.isInfoEnabled()).thenReturn(true);
        final Object sample = Map.of("key", "value");
        auditionLogger.info(logger, "Test object: {}", sample);
        verify(logger).info("Test object: {}", sample);
    }

    @Test
    void shouldLogDebugWhenEnabled() {
        when(logger.isDebugEnabled()).thenReturn(true);
        auditionLogger.debug(logger, "Debug log");
        verify(logger).debug("Debug log");
    }

    @Test
    void shouldLogWarnWhenEnabled() {
        when(logger.isWarnEnabled()).thenReturn(true);
        auditionLogger.warn(logger, "Warn log");
        verify(logger).warn("Warn log");
    }

    @Test
    void shouldLogErrorWhenEnabled() {
        when(logger.isErrorEnabled()).thenReturn(true);
        auditionLogger.error(logger, "Error log");
        verify(logger).error("Error log");
    }

    @Test
    void shouldLogErrorWithException() {
        when(logger.isErrorEnabled()).thenReturn(true);
        final Exception ex = new RuntimeException("Something went wrong");
        auditionLogger.logErrorWithException(logger, "Exception occurred", ex);
        verify(logger).error("Exception occurred", ex);
    }

    @Test
    void shouldLogHttpStatusCodeErrorAsJson() {
        when(logger.isErrorEnabled()).thenReturn(true);
        auditionLogger.logHttpStatusCodeError(logger, "Bad Request", 400);
        verify(logger).error(contains("\"errorCode\":400"));
    }

    @Test
    void shouldLogStandardProblemDetailAsJson() {
        when(logger.isErrorEnabled()).thenReturn(true);
        final ProblemDetail pd = ProblemDetail.forStatus(400);
        pd.setTitle("Invalid input");
        pd.setDetail("Field X is required");
        pd.setInstance(URI.create("/posts"));
        final Exception e = new IllegalArgumentException("bad input");

        auditionLogger.logStandardProblemDetail(logger, pd, e);
        verify(logger).error(contains("\"title\":\"Invalid input\""), eq(e));
    }
}

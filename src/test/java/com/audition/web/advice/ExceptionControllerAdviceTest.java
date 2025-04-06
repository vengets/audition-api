package com.audition.web.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.HttpClientErrorException;

class ExceptionControllerAdviceTest {

    private transient ExceptionControllerAdvice advice;
    private transient AuditionLogger mockLogger;

    @BeforeEach
    void setUp() {
        mockLogger = mock(AuditionLogger.class);
        advice = new ExceptionControllerAdvice();
        try {
            final var field = ExceptionControllerAdvice.class.getDeclaredField("logger");
            field.setAccessible(true);
            field.set(advice, mockLogger);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException("Failed to inject mock logger", e);
        }
    }

    @Test
    void shouldHandleHttpClientErrorException() {
        final HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", null,
            null,
            null);
        final ProblemDetail detail = advice.handleHttpClientException(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), detail.getStatus());
        assertEquals("400 Bad Request", detail.getDetail());
        assertEquals("API Error Occurred", detail.getTitle());
    }

    @Test
    void shouldHandleSystemExceptionWithMappedStatus() {
        final SystemException ex = new SystemException("Invalid input", "Validation Error", 400);
        final ProblemDetail detail = advice.handleSystemException(ex);

        assertEquals(400, detail.getStatus());
        assertEquals("Invalid input", detail.getDetail());
        assertEquals("Validation Error", detail.getTitle());
        verify(mockLogger, times(1)).error(any(), contains("SystemException occurred"));
    }

    @Test
    void shouldHandleSystemExceptionWithInvalidStatusCode() {
        final SystemException ex = new SystemException("Unknown code", "Error", 999);
        final ProblemDetail detail = advice.handleSystemException(ex);

        assertEquals(999, detail.getStatus());
        assertEquals("Unknown code", detail.getDetail());
        assertEquals("Error", detail.getTitle());
    }

    @Test
    void shouldHandleGenericException() {
        final Exception ex = new RuntimeException("Some unexpected failure");
        final ProblemDetail detail = advice.handleMainException(ex);

        assertEquals(500, detail.getStatus());
        assertEquals("Some unexpected failure", detail.getDetail());
        assertEquals("API Error Occurred", detail.getTitle());
        verify(mockLogger, times(1)).error(any(), contains("Unhandled exception occurred"));

    }

    @Test
    void shouldHandleBlankMessageWithDefaultMessage() {
        final Exception ex = new RuntimeException();
        final ProblemDetail detail = advice.handleMainException(ex);

        assertEquals(500, detail.getStatus());
        assertEquals("API Error occurred. Please contact support or administrator.", detail.getDetail());
    }

    @Test
    void shouldHandleHttpRequestMethodNotSupported() {
        final Exception ex = new HttpRequestMethodNotSupportedException("DELETE");
        final ProblemDetail detail = advice.handleMainException(ex);

        assertEquals(405, detail.getStatus());
        assertEquals("Request method 'DELETE' is not supported", detail.getDetail());
    }
}

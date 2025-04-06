package com.audition.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SystemExceptionTest {

    @Test
    void shouldCreateDefaultException() {
        final SystemException ex = new SystemException();
        assertNull(ex.getMessage());
        assertEquals("API Error Occurred", ex.getTitle());
    }

    @Test
    void shouldCreateWithMessageOnly() {
        final SystemException ex = new SystemException("Something went wrong");
        assertEquals("Something went wrong", ex.getMessage());
        assertEquals("API Error Occurred", ex.getTitle());
        assertNull(ex.getStatusCode());
    }

    @Test
    void shouldCreateWithMessageAndErrorCode() {
        final SystemException ex = new SystemException("Bad Request", 400);
        assertEquals("Bad Request", ex.getMessage());
        assertEquals(400, ex.getStatusCode());
    }

    @Test
    void shouldCreateWithMessageAndCause() {
        final Throwable cause = new RuntimeException("Underlying cause");
        final SystemException ex = new SystemException("Something failed", cause);
        assertEquals("Something failed", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void shouldCreateWithDetailTitleAndErrorCode() {
        final SystemException ex = new SystemException("Specific error", "Validation Error", 422);
        assertEquals("Specific error", ex.getMessage());
        assertEquals("Specific error", ex.getDetail());
        assertEquals("Validation Error", ex.getTitle());
        assertEquals(422, ex.getStatusCode());
    }

    @Test
    void shouldCreateWithDetailTitleAndCause() {
        final Throwable cause = new RuntimeException("deep");
        final SystemException ex = new SystemException("Unexpected detail", "Internal Error", cause);
        assertEquals("Unexpected detail", ex.getMessage());
        assertEquals("Unexpected detail", ex.getDetail());
        assertEquals("Internal Error", ex.getTitle());
        assertEquals(500, ex.getStatusCode());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void shouldCreateWithDetailStatusCodeAndCause() {
        final Throwable cause = new RuntimeException("root cause");
        final SystemException ex = new SystemException("Detail info", 503, cause);
        assertEquals("Detail info", ex.getMessage());
        assertEquals("Detail info", ex.getDetail());
        assertEquals("API Error Occurred", ex.getTitle());
        assertEquals(503, ex.getStatusCode());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void shouldCreateWithAllFields() {
        final Throwable cause = new IllegalStateException("test");
        final SystemException ex = new SystemException("Detailed problem", "Custom Title", 418, cause);
        assertEquals("Detailed problem", ex.getMessage());
        assertEquals("Detailed problem", ex.getDetail());
        assertEquals("Custom Title", ex.getTitle());
        assertEquals(418, ex.getStatusCode());
        assertEquals(cause, ex.getCause());
    }
}

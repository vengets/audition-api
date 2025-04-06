package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;


class RestTemplateLoggingInterceptorTest {

    @SuppressWarnings("PMD.LoggerIsNotStaticFinal")
    private transient Logger mockLogger;
    private static RestTemplateLoggingInterceptor interceptor;
    private static HttpRequest mockHttpReq;
    private static ClientHttpRequestExecution mockExec;
    private static ClientHttpResponse mockHttpResponse;
    private static final String END_POINT = "http://localhost/test";

    @SneakyThrows
    @BeforeEach
    void setUp() {
        mockLogger = mock(Logger.class);
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        interceptor = new RestTemplateLoggingInterceptor(mockLogger);
        mockHttpReq = mock(HttpRequest.class);
        mockExec = mock(ClientHttpRequestExecution.class);
        mockHttpResponse = mock(ClientHttpResponse.class);

        when(mockHttpReq.getURI()).thenReturn(URI.create(END_POINT));
        when(mockHttpReq.getMethod()).thenReturn(HttpMethod.POST);
        when(mockHttpReq.getHeaders()).thenReturn(new HttpHeaders());

        when(mockHttpResponse.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(mockHttpResponse.getStatusText()).thenReturn("OK");
        when(mockHttpResponse.getHeaders()).thenReturn(new HttpHeaders());
        when(mockHttpResponse.getBody()).thenReturn(new ByteArrayInputStream("response body".getBytes()));

    }

    @SneakyThrows
    @Test
    void shouldNotChangeHTTPResponse() {
        final byte[] body = "request body".getBytes();
        when(mockExec.execute(mockHttpReq, body)).thenReturn(mockHttpResponse);

        try (ClientHttpResponse actualHttpResponse = interceptor.intercept(mockHttpReq, body, mockExec)) {
            assertSame(mockHttpResponse, actualHttpResponse);
        }
    }

    @SneakyThrows
    @Test
    void shouldCallClientHttpResponseExecute() {
        final byte[] body = "request body".getBytes();
        when(mockExec.execute(mockHttpReq, body)).thenReturn(mockHttpResponse);

        interceptor.intercept(mockHttpReq, body, mockExec);

        verify(mockExec, times(1)).execute(mockHttpReq, body);
    }

    @SneakyThrows
    @Test
    void shouldLogHttpRequestDetails() {
        final byte[] body = "test body".getBytes();
        when(mockExec.execute(mockHttpReq, body)).thenReturn(mockHttpResponse);
        interceptor.intercept(mockHttpReq, body, mockExec);

        final ArgumentCaptor<String> formattedCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);

        verify(mockLogger, atLeastOnce()).info(formattedCaptor.capture(), argumentCaptor.capture());

        final List<String> messageTemplates = formattedCaptor.getAllValues();
        final List<Object> args = argumentCaptor.getAllValues();

        final boolean foundUrl = hasLog(messageTemplates, args, "URI", END_POINT);
        final boolean foundMethod = hasLog(messageTemplates, args, "Method", "POST");
        final boolean foundHeaders = hasLog(messageTemplates, args, "Headers", ""); // Only checks key
        final boolean foundRequestBody = hasLog(messageTemplates, args, "Request body", "test body");

        assertTrue(foundUrl, String.format("Expected 'URL: {}' to be logged", END_POINT));
        assertTrue(foundMethod, "Expected 'Method: POST' to be logged");
        assertTrue(foundHeaders, "Expected 'Headers' to be logged");
        assertTrue(foundRequestBody, "Expected 'Request body: test body' to be logged");
    }

    @SneakyThrows
    @Test
    void shouldLogHttpResponse() {
        final byte[] body = "response test".getBytes();
        when(mockExec.execute(mockHttpReq, body)).thenReturn(mockHttpResponse);

        interceptor.intercept(mockHttpReq, body, mockExec);

        final ArgumentCaptor<String> formattedCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);

        verify(mockLogger, atLeastOnce()).info(formattedCaptor.capture(), argumentCaptor.capture());

        final List<String> messageTemplates = formattedCaptor.getAllValues();
        final List<Object> args = argumentCaptor.getAllValues();

        final boolean foundStatusCode = hasLog(messageTemplates, args, "Status code", "200"); // OK = 200
        final boolean foundStatusText = hasLog(messageTemplates, args, "Status text", "OK");
        final boolean foundResponseHeaders = hasLog(messageTemplates, args, "Headers", "");
        final boolean foundResponseBody = hasLog(messageTemplates, args, "Response body", "response body");

        assertTrue(foundStatusCode, "Expected 'Status code: 200' to be logged");
        assertTrue(foundStatusText, "Expected 'Status text: OK' to be logged");
        assertTrue(foundResponseHeaders, "Expected 'Headers' to be logged");
        assertTrue(foundResponseBody, "Expected 'Response body: response body' to be logged");
    }

    private boolean hasLog(final List<String> templates, final List<Object> args, final String key,
        final String value) {
        return IntStream.range(0, templates.size())
            .anyMatch(i -> templates.get(i).contains(key) && String.valueOf(args.get(i)).contains(value));
    }
}
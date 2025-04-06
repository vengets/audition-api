package com.audition.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    @SuppressWarnings("PMD.LoggerIsNotStaticFinal")
    private transient Logger logger;

    public RestTemplateLoggingInterceptor() {
        this(LoggerFactory.getLogger(RestTemplateLoggingInterceptor.class));
    }

    RestTemplateLoggingInterceptor(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest request,
        final byte[] body,
        final ClientHttpRequestExecution execution) throws IOException {

        logRequest(request, body);
        final ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(final HttpRequest request, final byte[] body) {
        if (logger.isInfoEnabled()) {
            logger.info("=== Request Begin ===");
            logger.info("URI         : {}", request.getURI());
            logger.info("Method      : {}", request.getMethod());
            logger.info("Headers     : {}", request.getHeaders());
            logger.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
            logger.info("=== Request End ===");
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private void logResponse(final ClientHttpResponse response) throws IOException {
        final String body = getResponseBody(response);
        if (logger.isInfoEnabled()) {
            logger.info("=== Response Begin ===");
            logger.info("Status code  : {}", response.getStatusCode());
            logger.info("Status text  : {}", response.getStatusText());
            logger.info("Headers      : {}", response.getHeaders());
            logger.info("Response body: {}", body);
            logger.info("=== Response End ===");
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private static String getResponseBody(final ClientHttpResponse response) throws IOException {
        final String body;
        try (BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            final StringBuilder inputStringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            body = inputStringBuilder.toString();
        }
        return body;
    }

}

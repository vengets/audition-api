package com.audition.configuration;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ResponseHeaderInjector extends OncePerRequestFilter {

    @Autowired
    private transient Tracer tracer;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain) throws ServletException, IOException {
        final Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            final TraceContext spanContext = currentSpan.context();
            response.setHeader("X-Trace-Id", spanContext.traceId());
            response.setHeader("X-Span-Id", spanContext.spanId());
        }
        filterChain.doFilter(request, response);
    }

}

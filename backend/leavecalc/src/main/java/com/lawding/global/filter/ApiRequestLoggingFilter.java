package com.lawding.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiRequestLoggingFilter extends OncePerRequestFilter {

    public static final String TRACE_ID = "traceId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        long startedAt = System.nanoTime();
        boolean exceptionLogged = false;
        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);

        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException | Error ex) {
            exceptionLogged = true;
            logSystemError(request, response, startedAt, ex);
            throw ex;
        } finally {
            if (!exceptionLogged) {
                logCompletedRequest(request, response, startedAt);
            }
            MDC.remove(TRACE_ID);
        }
    }

    private void logCompletedRequest(
        HttpServletRequest request,
        HttpServletResponse response,
        long startedAt
    ) {
        int status = response.getStatus();
        long elapsedMs = elapsedMs(startedAt);

        if (status >= 500) {
            log.error("[SYSTEM_ERROR] method={}, uri={}, status={}, elapsedMs={}, clientIp={}",
                request.getMethod(), request.getRequestURI(), status, elapsedMs,
                resolveClientIp(request));
        } else if (status >= 400) {
            log.warn("[REQUEST_FAILURE] method={}, uri={}, status={}, elapsedMs={}, clientIp={}",
                request.getMethod(), request.getRequestURI(), status, elapsedMs,
                resolveClientIp(request));
        } else {
            log.info("[REQUEST_SUCCESS] method={}, uri={}, status={}, elapsedMs={}, clientIp={}",
                request.getMethod(), request.getRequestURI(), status, elapsedMs,
                resolveClientIp(request));
        }
    }

    private void logSystemError(
        HttpServletRequest request,
        HttpServletResponse response,
        long startedAt,
        Throwable throwable
    ) {
        log.error(
            "[SYSTEM_ERROR] method={}, uri={}, status={}, elapsedMs={}, clientIp={}, exception={}",
            request.getMethod(),
            request.getRequestURI(),
            response.getStatus() >= 500 ? response.getStatus() : 500,
            elapsedMs(startedAt),
            resolveClientIp(request),
            throwable.getClass().getSimpleName(),
            throwable
        );
    }

    private long elapsedMs(long startedAt) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",", 2)[0].trim();
        }
        return request.getRemoteAddr();
    }
}

package com.lawding.global.filter;

import com.lawding.global.common.enums.Platform;
import com.lawding.global.exception.ErrorCode;
import com.lawding.global.security.SecurityErrorResponseWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
public class PlatformHeaderFilter extends OncePerRequestFilter {

    public static final String PLATFORM_HEADER = "X-Platform";

    private final SecurityErrorResponseWriter errorResponseWriter;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String platform = request.getHeader(PLATFORM_HEADER);

        if (platform == null || platform.isBlank()) {
            errorResponseWriter.write(request, response, ErrorCode.MISSING_X_PLATFORM_HEADER);
            return;
        }

        if (!Platform.supports(platform)) {
            errorResponseWriter.write(request, response, ErrorCode.INVALID_PLATFORM_HEADER);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return path.startsWith("/oauth2/") || path.startsWith("/login/oauth2/code/");
    }
}

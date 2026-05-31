package com.lawding.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class AppleDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        if (request.getRequestURI().contains("/login/oauth2/code/apple")) {
            log.error("🍎 [애플 리다이렉트 도착!] URI: {}", request.getRequestURI());
            log.error("🍎 Method: {}", request.getMethod());
            log.error("🍎 Session ID: {}", request.getSession().getId());

            // 넘어온 모든 파라미터(Form 데이터 포함) 찍기
            request.getParameterMap().forEach((key, value) ->
                log.error("🍎 Parameter [{}]: {}", key, String.join(",", value))
            );
        }

        filterChain.doFilter(request, response);
    }
}
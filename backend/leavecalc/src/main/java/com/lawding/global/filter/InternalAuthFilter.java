package com.lawding.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class InternalAuthFilter extends HttpFilter {

    private final String internalSecret;

    public InternalAuthFilter(String internalSecret) {
        this.internalSecret = internalSecret;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        // 1. 현재 들어온 요청의 주소(URI)를 확인합니다.
        String requestURI = request.getRequestURI();

        // 예외 처리: 소셜 로그인 관련 주소는 헤더 검사 없이 무조건 통과
        if (requestURI.startsWith("/oauth2/") ||
            requestURI.startsWith("/login/oauth2/code/") ||
            requestURI.equals("/test/login-success")) { // 테스트용 url

            chain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("X-Internal-Auth");

        log.info("InternalAuthFilter 실행: X-Internal-Auth={}, remoteAddr={}",
            header, request.getRemoteAddr());
        if (header == null || !header.equals(internalSecret)) {
            log.warn("인증 실패: 잘못된 X-Internal-Auth 헤더");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }
        chain.doFilter(request, response);
    }
}

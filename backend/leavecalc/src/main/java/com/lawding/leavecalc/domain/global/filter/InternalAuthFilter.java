package com.lawding.leavecalc.domain.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
public class InternalAuthFilter extends HttpFilter {

    private final String internalSecret; // ✅ 생성자 주입

    public InternalAuthFilter(String internalSecret) {
        this.internalSecret = internalSecret;
    }
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("X-Internal-Auth");

        log.info("InternalAuthFilter 실행: X-Internal-Auth={}, remoteAddr={}",
            header, request.getRemoteAddr());
        log.info("비교 값  = {} ", internalSecret);
        if (header == null || !header.equals(internalSecret)) {
            log.warn("인증 실패: 잘못된 X-Internal-Auth 헤더");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }
        log.debug("정상적인 요청 인증");
        chain.doFilter(request, response);
    }
}

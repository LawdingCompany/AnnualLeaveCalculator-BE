package com.lawding.leavecalc.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;

public class InternalAuthFilter extends HttpFilter {

    @Value("${internal.secret")
    private String internalSecret;


    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        String header= request.getHeader("X-Internal-Auth");

        if(header==null || !header.equals(internalSecret)){
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Forbidden");
            return;
        }

        chain.doFilter(request,response);
    }
}

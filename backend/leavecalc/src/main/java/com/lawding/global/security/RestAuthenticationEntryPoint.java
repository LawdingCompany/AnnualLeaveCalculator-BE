package com.lawding.global.security;

import com.lawding.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String AUTH_ERROR_CODE_ATTRIBUTE = "auth.errorCode";

    private final SecurityErrorResponseWriter responseWriter;

    public RestAuthenticationEntryPoint(SecurityErrorResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        Object errorCode = request.getAttribute(AUTH_ERROR_CODE_ATTRIBUTE);
        responseWriter.write(
            request,
            response,
            errorCode instanceof ErrorCode ? (ErrorCode) errorCode : ErrorCode.UNAUTHORIZED
        );
    }
}

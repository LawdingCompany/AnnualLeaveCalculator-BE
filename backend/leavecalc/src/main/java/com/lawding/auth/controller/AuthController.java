package com.lawding.auth.controller;

import com.lawding.auth.dto.request.RefreshTokenRequest;
import com.lawding.auth.dto.request.TestTokenRequest;
import com.lawding.auth.dto.response.TokenResponse;
import com.lawding.auth.service.AuthService;
import com.lawding.global.common.dto.response.ApiResponse;
import com.lawding.global.exception.ClientException;
import com.lawding.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
        @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("[req] POST /auth/reissue - token reissue request");
        TokenResponse tokenResponse = authService.reissue(refreshTokenRequest.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(tokenResponse));
    }

    @PostMapping("/test-token")
    public ResponseEntity<ApiResponse<TokenResponse>> issueTestToken(
        @RequestHeader(value = "X-Test", required = false) String testHeader,
        @RequestBody TestTokenRequest request
    ) {
        if (!"true".equalsIgnoreCase(testHeader)) {
            throw new ClientException(ErrorCode.UNAUTHORIZED, "테스트 토큰 발급은 X-Test: true 헤더가 필요합니다.");
        }

        return ResponseEntity.ok(ApiResponse.ok(
            authService.issueTestToken(request.userId(), request.email())
        ));
    }

}

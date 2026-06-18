package com.lawding.auth.controller;

import com.lawding.auth.dto.request.RefreshTokenRequest;
import com.lawding.auth.dto.response.TokenResponse;
import com.lawding.auth.service.AuthService;
import com.lawding.global.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("[req] POST /auth/reissue - AT,RT 재발급 요청");
        TokenResponse tokenResponse = authService.reissue(refreshTokenRequest.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(tokenResponse));
    }

}

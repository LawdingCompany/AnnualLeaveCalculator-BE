package com.lawding.auth.service.impl;

import com.lawding.auth.dto.response.TokenResponse;
import com.lawding.auth.entity.User;
import com.lawding.auth.jwt.JwtProvider;
import com.lawding.auth.repository.AuthRepository;
import com.lawding.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final AuthRepository authRepository;

    @Transactional
    @Override
    public TokenResponse reissue(String refreshToken) {
        // 1. RT 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }
        // 2. RT에서 사용자 정보 추출
        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        log.debug("토큰 재발급 요청 ID = {}", userId);

        // 3. [보안 강화] DB의 RT와 비교 (가장 중요한 부분!)
        User user = authRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 3. 새로운 AT,RT 생성
        String newAccessToken = jwtProvider.createAccessToken(userId);
        String newRefreshToken = jwtProvider.createRefreshToken(userId);

        user.updateRefreshToken(newRefreshToken);

        return TokenResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .onboardingCompleted(user.getOnboardingCompleted())
            .build();
    }
}

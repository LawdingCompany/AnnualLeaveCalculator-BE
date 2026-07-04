package com.lawding.auth.handler;

import com.lawding.auth.dto.CustomOAuth2User;
import com.lawding.auth.entity.User;
import com.lawding.auth.jwt.JwtProvider;
import com.lawding.auth.repository.AuthRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final AuthRepository authRepository;

    @Value("${app.oauth.redirect-url}")
    private String appRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        // 1. 서비스에서 넘겨준 캡슐 객체로 캐스팅
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customOAuth2User.getUser().getId();
        // 2. 캡슐 안에서 우리 서비스의 진짜 유저 객체만 쏙 빼기! (DB 조회 불필요✨)
        User user = customOAuth2User.getUser();
        // 3. 토큰 발급
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(userId); // RT 발급 추가!

        user.updateRefreshToken(refreshToken);
        authRepository.save(user);

        String targetUrl = UriComponentsBuilder.fromUriString(appRedirectUrl)
            .queryParam("accessToken", accessToken)
            .queryParam("refreshToken", refreshToken)
            .queryParam("onboardingCompleted", user.getOnboardingCompleted())
            .build().toUriString();

        log.info("[Login Success] provider={}, userId={}", user.getProvider(), user.getId());
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

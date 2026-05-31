package com.lawding.user.handler;

import com.lawding.user.dto.CustomOAuth2User;
import com.lawding.user.entity.User;
import com.lawding.user.jwt.JwtProvider;
import com.lawding.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        // 1. 서비스에서 넘겨준 캡슐 객체로 캐스팅
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customOAuth2User.getUser().getId();
        // 2. 캡슐 안에서 우리 서비스의 진짜 유저 객체만 쏙 빼기! (DB 조회 불필요✨)
        User user = customOAuth2User.getUser();
        // 3. 토큰 발급
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(userId); // RT 발급 추가!
//        // 4. 플러터 앱을 깨우는 커스텀 스킴 주소로 변경! (lawding은 예시, 원하는 이름으로 설정 가능)
//        String targetUrl = UriComponentsBuilder.fromUriString("lawding://login/success")
//            .queryParam("token", accessToken)
//            .queryParam("refreshToken", refreshToken)
//            .build().toUriString();

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        // 🚧 [테스트용] 앱 주소 대신 로컬 웹 주소로 리다이렉트!
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/test/login-success")
            .queryParam("accessToken", accessToken)
            .queryParam("refreshToken", refreshToken)
            .build().toUriString();

        log.info("[Login Success] provider={}, userId={}", user.getProvider(), user.getId());
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

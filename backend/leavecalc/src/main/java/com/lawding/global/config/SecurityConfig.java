package com.lawding.global.config;

import com.lawding.auth.client.CustomOAuth2AccessTokenClient;
import com.lawding.auth.filter.JwtAuthenticationFilter;
import com.lawding.auth.handler.OAuth2SuccessHandler;
import com.lawding.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2AccessTokenClient customOAuth2AccessTokenClient;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            // 🔥 URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
//                // 연차 계산기 관련 API와 기본 화면은 무조건 통과!
//                .requestMatchers("/", "/api/leavecalc/**", "/login**").permitAll()
//                // 캘린더 API는 반드시 소셜 로그인을 완료한 유저만 통과!
                .requestMatchers("/calendar-events/**").authenticated()
//                // 그 외 모든 요청도 일단 보호
//                .anyRequest().authenticated()
                    .anyRequest().permitAll()
            )

            .oauth2Login(oauth2 -> oauth2
                // 애플 통신을 위해 우리가 만든 커스텀 클라이언트 주입
                .redirectionEndpoint(redirection ->
                    redirection.baseUri("/login/oauth2/code/*"))
                .tokenEndpoint(token -> token.accessTokenResponseClient(
                    customOAuth2AccessTokenClient))
                // 성공적으로 정보를 받아왔을 때, 우리 DB에 저장하는 서비스 주입
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler((request, response, exception) -> {
                    log.error("[OAuth2 Login Fail] full stack", exception);

                    log.error("message = {}", exception.getMessage());

                    response.sendRedirect("/login?error");
                })
            )
            // ✨ 캘린더 접근 시 시큐리티 기본 필터보다 우리가 만든 JWT 문지기를 먼저 거치게 설정!
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

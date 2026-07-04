package com.lawding.global.config;

import com.lawding.auth.client.CustomOAuth2AccessTokenClient;
import com.lawding.auth.filter.JwtAuthenticationFilter;
import com.lawding.auth.handler.OAuth2SuccessHandler;
import com.lawding.auth.service.CustomOAuth2UserService;
import com.lawding.global.exception.ErrorCode;
import com.lawding.global.security.RestAccessDeniedHandler;
import com.lawding.global.security.RestAuthenticationEntryPoint;
import com.lawding.global.security.SecurityErrorResponseWriter;
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
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final SecurityErrorResponseWriter securityErrorResponseWriter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler))

            // 🔥 URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/calendar-events/**", "/users/**", "/dashboard/**").authenticated()
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
                    log.error("requestUri = {}", request.getRequestURI());
                    log.error("queryString = {}", request.getQueryString());
                    log.error("cookies = {}", request.getHeader("Cookie"));

                    securityErrorResponseWriter.write(request, response, ErrorCode.UNAUTHORIZED);
                })
            )
            // ✨ 캘린더 접근 시 시큐리티 기본 필터보다 우리가 만든 JWT 문지기를 먼저 거치게 설정!
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

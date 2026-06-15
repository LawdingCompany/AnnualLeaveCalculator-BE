package com.lawding.auth.client;

import com.lawding.auth.config.apple.AppleClientSecretGenerator;
import org.springframework.security.oauth2.client.endpoint.DefaultOAuth2TokenRequestParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
public class CustomOAuth2AccessTokenClient implements
    OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final RestClientAuthorizationCodeTokenResponseClient defaultClient;

    public CustomOAuth2AccessTokenClient(
        AppleClientSecretGenerator appleClientSecretGenerator) {
        this.defaultClient = new RestClientAuthorizationCodeTokenResponseClient();

        // 2. 파라미터(Body)만 전용으로 뽑아내는 최신 컨버터
        DefaultOAuth2TokenRequestParametersConverter<OAuth2AuthorizationCodeGrantRequest> converter =
            new DefaultOAuth2TokenRequestParametersConverter<>();

        // 3. 통신 직전에 파라미터를 가로채서 조작
        this.defaultClient.setParametersConverter(grantRequest -> {
            // 스프링이 만들어준 기본 파라미터 (절대 null이 아님)
            MultiValueMap<String, String> parameters = converter.convert(grantRequest);

            // 애플 로그인일 경우에만 방금 만든 동적 시크릿(JWT)을 끼워 넣기!
            if ("apple".equals(grantRequest.getClientRegistration().getRegistrationId())) {
                parameters.set("client_secret", appleClientSecretGenerator.createAppleClientSecret());
            }

            return parameters;
        });
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(
        OAuth2AuthorizationCodeGrantRequest grantRequest) {
        return defaultClient.getTokenResponse(grantRequest);
    }
}

/**
 * Apple OAuth 전용 Access Token 요청 클라이언트
 *
 * Apple은 Google, Kakao와 달리 client_secret을 설정 파일에 고정값으로
 * 저장하지 않고 JWT 형태로 동적으로 생성해야 한다.
 *
 * 따라서 Spring Security의 기본 토큰 요청 클라이언트를 확장하여
 * Apple 로그인 시 토큰 요청 직전에 client_secret을 생성하고
 * 요청 본문에 주입하도록 구현하였다.
 */

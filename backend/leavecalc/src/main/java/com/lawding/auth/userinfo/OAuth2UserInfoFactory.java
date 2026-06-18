package com.lawding.auth.userinfo;

import java.util.Map;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2UserInfoFactory {

    // 플랫폼 이름(google, kakao, apple)을 받아서 그에 맞는 알맞은 객체를 조립해 주는 공장 메서드
    public static OAuth2UserInfo getOAuth2UserInfo(String provider,
        Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if ("kakao".equals(provider)) {
            return new KakaoOAuth2UserInfo(attributes);
        } else if ("apple".equals(provider)) {
            return new AppleOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + provider);
        }
    }
}

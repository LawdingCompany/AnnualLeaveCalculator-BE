package com.lawding.user.userinfo;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        if (attributes.get("kakao_account") instanceof Map<?, ?> kakaoAccount) {
            return (String) kakaoAccount.get("email");
        }
        return null;
    }

    @Override
    public String getName() {
        if (attributes.get("kakao_account") instanceof Map<?, ?> kakaoAccount &&
            kakaoAccount.get("profile") instanceof Map<?, ?> profile) {
            return (String) profile.get("nickname");
        }
        return null;
    }
}

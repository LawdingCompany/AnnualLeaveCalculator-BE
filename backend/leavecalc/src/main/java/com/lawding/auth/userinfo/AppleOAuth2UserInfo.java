package com.lawding.auth.userinfo;

import java.util.Map;

public class AppleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "apple";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getUsername() {
        // 애플은 최초 로그인 때만 name 제공
        // 이후엔 null이므로 DB에서 기존 이름 유지
        Object name = attributes.get("name");
        if (name instanceof Map) {
            Map<?, ?> nameMap = (Map<?, ?>) name;
            String firstName = (String) nameMap.get("firstName");
            String lastName = (String) nameMap.get("lastName");
            if (firstName != null || lastName != null) {
                return (lastName != null ? lastName : "") + (firstName != null ? firstName : "");
            }
        }
        // name 없으면 null 반환 → saveOrUpdate에서 기존 이름 유지
        return null;
    }
}
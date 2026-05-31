package com.lawding.user.dto;

import com.lawding.user.entity.User;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final User user;

    public CustomOAuth2User(OAuth2User oAuth2User, User user) {
        // 부모 클래스(DefaultOAuth2User)에게 권한과 속성값들을 전달 ("email"은 기준이 되는 키)
        super(oAuth2User.getAuthorities(), oAuth2User.getAttributes(), "email");
        this.user = user;
    }
}

package com.lawding.auth.dto;

import com.lawding.auth.entity.User;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final User user;

    public CustomOAuth2User(OAuth2User oAuth2User, User user, String userNameAttributeName) {
        // "email" 대신 받아온 식별자 키를 그대로 넘겨줍니다.
        super(oAuth2User.getAuthorities(), oAuth2User.getAttributes(), userNameAttributeName);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

package com.lawding.auth.userinfo;

public interface OAuth2UserInfo {

    String getProvider(); // "google", "kakao", "apple"

    String getEmail();

    String getUsername();
}

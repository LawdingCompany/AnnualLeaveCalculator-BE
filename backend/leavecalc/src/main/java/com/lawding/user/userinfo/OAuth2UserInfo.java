package com.lawding.user.userinfo;

public interface OAuth2UserInfo {

    String getProvider(); // "google", "kakao", "apple"

    String getEmail();

    String getName();
}

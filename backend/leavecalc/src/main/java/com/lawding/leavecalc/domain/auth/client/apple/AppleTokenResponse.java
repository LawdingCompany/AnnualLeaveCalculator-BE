package com.lawding.leavecalc.domain.auth.client.apple;

public record AppleTokenResponse(
    String accessToken,
    String idToken,
    String refreshToken,
    String tokenType,
    int expiresIn
) {

}

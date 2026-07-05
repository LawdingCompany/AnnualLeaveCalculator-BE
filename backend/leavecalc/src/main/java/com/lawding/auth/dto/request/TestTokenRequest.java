package com.lawding.auth.dto.request;

public record TestTokenRequest(
    Long userId,
    String email
) {
}

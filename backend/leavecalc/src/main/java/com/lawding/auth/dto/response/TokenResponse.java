package com.lawding.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse(
    String accessToken,
    String refreshToken,
    Boolean onboardingCompleted
) {
}

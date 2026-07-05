package com.lawding.calendar.user.dto.response;

import com.lawding.auth.entity.User;

public record UserResponse(
    Long id,
    String username,
    String email,
    String provider,
    String nickname,
    Boolean onboardingCompleted,
    Boolean deleted
) {

    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getProvider(),
            user.getNickname(),
            user.getOnboardingCompleted(),
            user.isDeleted()
        );
    }
}

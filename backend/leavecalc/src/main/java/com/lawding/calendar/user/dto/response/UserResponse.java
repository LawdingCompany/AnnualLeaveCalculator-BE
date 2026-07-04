package com.lawding.calendar.user.dto.response;

import com.lawding.auth.entity.User;
import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String username,
    String email,
    String provider,
    String nickname,
    Boolean onboardingCompleted,
    Boolean deleted,
    LocalDateTime deletedAt,
    LocalDateTime hardDeleteScheduledAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getProvider(),
            user.getNickname(),
            user.getOnboardingCompleted(),
            user.isDeleted(),
            user.getDeletedAt(),
            user.getHardDeleteScheduledAt(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}

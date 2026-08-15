package com.lawding.notification.dto;

import com.lawding.notification.entity.UserNotification;
import java.time.LocalDateTime;

public record UserNotificationResponse(Long id, String type, String title, String message,
    Integer grantedMinutes, Boolean isRead, LocalDateTime createdAt) {
    public static UserNotificationResponse from(UserNotification n) {
        return new UserNotificationResponse(n.getId(), n.getType(), n.getTitle(), n.getMessage(),
            n.getGrantedMinutes(), n.getIsRead(), n.getCreatedAt());
    }
}

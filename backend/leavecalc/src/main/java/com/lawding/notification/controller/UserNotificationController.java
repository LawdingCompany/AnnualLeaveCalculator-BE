package com.lawding.notification.controller;

import com.lawding.global.common.dto.response.ApiResponse;
import com.lawding.global.exception.ClientException;
import com.lawding.global.exception.ErrorCode;
import com.lawding.notification.dto.UserNotificationResponse;
import com.lawding.notification.entity.UserNotification;
import com.lawding.notification.repository.UserNotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/notifications")
public class UserNotificationController {
    private final UserNotificationRepository repository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<UserNotificationResponse>>> getNotifications(
        @AuthenticationPrincipal Long userId) {
        if (userId == null) throw new ClientException(ErrorCode.UNAUTHORIZED);
        return ResponseEntity.ok(ApiResponse.ok(repository.findAllByUser_IdOrderByCreatedAtDesc(userId)
            .stream().map(UserNotificationResponse::from).toList()));
    }

    @PatchMapping("/{notificationId}/read")
    @Transactional
    public ResponseEntity<ApiResponse<UserNotificationResponse>> read(
        @AuthenticationPrincipal Long userId, @PathVariable Long notificationId) {
        UserNotification notification = repository.findByIdAndUser_Id(notificationId, userId)
            .orElseThrow(() -> new ClientException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.markRead();
        return ResponseEntity.ok(ApiResponse.ok(UserNotificationResponse.from(notification)));
    }
}

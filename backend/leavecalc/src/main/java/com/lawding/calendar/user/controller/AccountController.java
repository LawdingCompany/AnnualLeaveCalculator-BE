package com.lawding.calendar.user.controller;

import com.lawding.calendar.user.dto.request.UserNicknameRequest;
import com.lawding.calendar.user.dto.response.UserResponse;
import com.lawding.calendar.user.service.UserService;
import com.lawding.global.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/profile")
public class AccountController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUser(userId)));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
        @AuthenticationPrincipal Long userId,
        @RequestBody UserNicknameRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(userService.updateUser(userId, request)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteProfile(@AuthenticationPrincipal Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.okMessage("사용자 삭제가 예약되었습니다."));
    }

    @PostMapping("/restore")
    public ResponseEntity<ApiResponse<UserResponse>> restoreProfile(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.cancelDeleteUser(userId)));
    }
}

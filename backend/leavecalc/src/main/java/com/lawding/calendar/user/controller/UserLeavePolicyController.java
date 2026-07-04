package com.lawding.calendar.user.controller;

import com.lawding.calendar.user.dto.request.UserLeavePolicyRequest;
import com.lawding.calendar.user.dto.response.UserLeavePolicyResponse;
import com.lawding.calendar.user.service.UserService;
import com.lawding.global.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/leave-policy")
public class UserLeavePolicyController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserLeavePolicyResponse>> getUserLeavePolicy(
        @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserLeavePolicy(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveUserLeavePolicy(
        @AuthenticationPrincipal Long userId,
        @RequestBody UserLeavePolicyRequest request
    ) {
        userService.saveUserLeavePolicy(userId, request);
        return ResponseEntity.ok(ApiResponse.okMessage("사용자 연차 정책이 저장되었습니다."));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserLeavePolicyResponse>> updateUserLeavePolicy(
        @AuthenticationPrincipal Long userId,
        @RequestBody UserLeavePolicyRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(userService.updateUserLeavePolicy(userId, request)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUserLeavePolicy(@AuthenticationPrincipal Long userId) {
        userService.deleteUserLeavePolicy(userId);
        return ResponseEntity.ok(ApiResponse.okMessage("사용자 연차 정책이 삭제되었습니다."));
    }
}

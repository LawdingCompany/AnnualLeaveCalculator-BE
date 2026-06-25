package com.lawding.calendar.user.controller;

import com.lawding.calendar.user.dto.request.UserLeavePolicyRequest;
import com.lawding.calendar.user.dto.response.DashboardResponse;
import com.lawding.calendar.user.service.UserService;
import com.lawding.global.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashBoard(
        @AuthenticationPrincipal Long userId) {
        log.info("[req] GET /users/me/dashboard - 사용자 대시보드 조회 요청, userId={}", userId);
        return ResponseEntity.ok(ApiResponse.ok(userService.getDashBoard(userId)));
    }

    @PostMapping("/leave-policy")
    public ResponseEntity<ApiResponse<Void>> saveUserLeavePolicy(
        @AuthenticationPrincipal Long userId,
        @RequestBody UserLeavePolicyRequest request) {
        log.info("[req] POST /users/leave-policy - 사용자 기본 정보 등록 요청, id = {}", userId);
        userService.saveUserLeavePolicy(userId, request);
        return ResponseEntity.ok(ApiResponse.okMessage("사용자 기본 정보가 등록되었습니다."));
    }

}

package com.lawding.calendar.user.controller;

import com.lawding.calendar.user.dto.request.UpdateRemainingLeaveMinutesRequest;
import com.lawding.calendar.user.dto.response.DashboardResponse;
import com.lawding.calendar.user.dto.response.LeaveYearlyBalanceResponse;
import com.lawding.calendar.user.service.UserService;
import com.lawding.global.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class LeaveYearlyBalanceController {

    private final UserService userService;

    @GetMapping("/me/leave-summary")
    public ResponseEntity<ApiResponse<DashboardResponse>> getLeaveSummary(
        @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getDashBoard(userId)));
    }

    @GetMapping("/leave-yearly-balance/latest")
    public ResponseEntity<ApiResponse<LeaveYearlyBalanceResponse>> getLatestLeaveYearlyBalance(
        @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getLatestLeaveYearlyBalance(userId)));
    }

    @PatchMapping("/leave-yearly-balance/remaining-minutes")
    public ResponseEntity<ApiResponse<LeaveYearlyBalanceResponse>> updateRemainingLeaveMinutes(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody UpdateRemainingLeaveMinutesRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            userService.updateRemainingLeaveMinutes(userId, request.remainingLeaveMinutes())
        ));
    }
}

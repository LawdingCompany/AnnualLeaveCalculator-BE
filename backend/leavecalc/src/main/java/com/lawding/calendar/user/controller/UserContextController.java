package com.lawding.calendar.user.controller;

import com.lawding.calendar.user.dto.response.UserContextResponse;
import com.lawding.calendar.user.service.UserService;
import com.lawding.global.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me")
public class UserContextController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserContextResponse>> getUserContext(
        @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserContext(userId)));
    }
}

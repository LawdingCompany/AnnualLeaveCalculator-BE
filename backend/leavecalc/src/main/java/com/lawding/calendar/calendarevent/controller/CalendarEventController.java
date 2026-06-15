package com.lawding.calendar.calendarevent.controller;

import com.lawding.calendar.calendarevent.dto.request.CalendarEventRequest;
import com.lawding.calendar.calendarevent.service.CalendarEventService;
import com.lawding.calendar.user.dto.request.UserLeavePolicyRequest;
import com.lawding.calendar.user.service.UserService;
import com.lawding.global.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar-events")
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createEvent(
        @AuthenticationPrincipal Long userId,
        @RequestBody CalendarEventRequest request) {
        log.info("[req] POST /calendar-events - 일정 등록 요청, userId={}", userId);
        calendarEventService.createEvent(userId, request);
        return ResponseEntity.ok(ApiResponse.okMessage("사용자 기본 정보가 등록되었습니다."));
    }
}


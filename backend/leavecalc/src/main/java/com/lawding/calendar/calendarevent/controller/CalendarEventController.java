package com.lawding.calendar.calendarevent.controller;

import com.lawding.calendar.calendarevent.dto.request.CalendarEventRequest;
import com.lawding.calendar.calendarevent.dto.response.CalendarEventCreateResponse;
import com.lawding.calendar.calendarevent.dto.response.CalendarEventResponse;
import com.lawding.calendar.calendarevent.service.CalendarEventService;
import com.lawding.global.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar-events")
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    @PostMapping
    public ResponseEntity<ApiResponse<CalendarEventCreateResponse>> createEvent(
        @AuthenticationPrincipal Long userId,
        @RequestBody @Valid CalendarEventRequest request) {
        log.info("[req] POST /calendar-events - 일정 등록 요청, userId={}", userId);
        return ResponseEntity.ok(
            ApiResponse.ok(CalendarEventCreateResponse.from(
                calendarEventService.createEvent(userId, request)
            ))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CalendarEventResponse>>> getEvents(
        @AuthenticationPrincipal Long userId,
        @RequestParam @Min(value = 1900, message = "year는 1900 이상이어야 합니다.") int year,
        @RequestParam @Min(value = 1, message = "month는 1 이상이어야 합니다.")
        @Max(value = 12, message = "month는 12 이하여야 합니다.") int month) {
        log.info("[req] GET /calendar-events?year={}&month={} - 월별 일정 목록 조회 요청, userId={}",
            year, month, userId);
        return ResponseEntity.ok(
            ApiResponse.ok(
                calendarEventService.findEventsByMonth(userId, year, month).stream()
                    .map(CalendarEventResponse::from)
                    .toList()
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CalendarEventResponse>> getEvent(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long id) {
        log.info("[req] GET /calendar-events/{} - 일정 단건 조회 요청, userId={}", id, userId);
        return ResponseEntity.ok(
            ApiResponse.ok(CalendarEventResponse.from(calendarEventService.findEvent(userId, id)))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateEvent(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long id,
        @RequestBody @Valid CalendarEventRequest request) {
        log.info("[req] PUT /calendar-events/{} - 일정 수정 요청, userId={}", id, userId);
        calendarEventService.updateEvent(userId, id, request);
        return ResponseEntity.ok(ApiResponse.okMessage("일정이 수정되었습니다."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long id) {
        log.info("[req] DELETE /calendar-events/{} - 일정 삭제 요청, userId={}", id, userId);
        calendarEventService.deleteEvent(userId, id);
        return ResponseEntity.ok(ApiResponse.okMessage("일정이 삭제되었습니다."));
    }
}

package com.lawding.calendar.recommendation.controller;

import com.lawding.calendar.recommendation.dto.response.RecommendedScheduleResponse;
import com.lawding.calendar.recommendation.service.RecommendedScheduleService;
import com.lawding.global.common.dto.response.ApiResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommended-schedules")
public class RecommendedScheduleController {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    private final RecommendedScheduleService recommendedScheduleService;

    @GetMapping("/nearest")
    public ResponseEntity<ApiResponse<RecommendedScheduleResponse>> getNearestSchedule() {
        RecommendedScheduleResponse response = recommendedScheduleService
            .findNearest(LocalDate.now(KOREA_ZONE))
            .map(RecommendedScheduleResponse::from)
            .orElse(null);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

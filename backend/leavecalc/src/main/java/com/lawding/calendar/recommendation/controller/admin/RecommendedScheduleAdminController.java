package com.lawding.calendar.recommendation.controller.admin;

import com.lawding.calendar.recommendation.dto.request.RecommendedScheduleRequest;
import com.lawding.calendar.recommendation.dto.response.RecommendedScheduleResponse;
import com.lawding.calendar.recommendation.service.RecommendedScheduleService;
import com.lawding.global.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/recommended-schedules")
public class RecommendedScheduleAdminController {

    private final RecommendedScheduleService recommendedScheduleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecommendedScheduleResponse>>> getAllSchedules() {
        return ResponseEntity.ok(ApiResponse.ok(
            recommendedScheduleService.findAll().stream()
                .map(RecommendedScheduleResponse::from)
                .toList()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecommendedScheduleResponse>> getSchedule(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            RecommendedScheduleResponse.from(recommendedScheduleService.findById(id))
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecommendedScheduleResponse>> createSchedule(
        @Valid @RequestBody RecommendedScheduleRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(
            RecommendedScheduleResponse.from(recommendedScheduleService.create(request))
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecommendedScheduleResponse>> updateSchedule(
        @PathVariable Long id,
        @Valid @RequestBody RecommendedScheduleRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            RecommendedScheduleResponse.from(recommendedScheduleService.update(id, request))
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long id) {
        recommendedScheduleService.delete(id);
        return ResponseEntity.ok(ApiResponse.okMessage("추천 일정이 삭제되었습니다."));
    }
}

package com.lawding.leavecalc.domain.stats.controller;

import com.lawding.leavecalc.domain.global.common.dto.response.ApiResponse;
import com.lawding.leavecalc.domain.stats.dto.DailyUserDto;
import com.lawding.leavecalc.domain.stats.service.UserStatsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/stats")
public class UserStatsController {

    private final UserStatsService userStatsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DailyUserDto>>> getAllDailyUser() {
        log.info("GET /stats - 모든 이용자 수 조회 요청");
        List<DailyUserDto> stats = userStatsService.getAllDailyUser();
        log.info("전체 일자별 이용자 수 조회 완료: 총 {}건", stats.size());
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}

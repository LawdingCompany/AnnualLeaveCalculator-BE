package com.lawding.calendar.holiday.controller;

import com.lawding.calendar.holiday.dto.response.HolidayResponse;
import com.lawding.calendar.holiday.service.HolidayService;
import com.lawding.global.common.dto.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping(params = "year")
    public ResponseEntity<ApiResponse<List<HolidayResponse>>> getHolidaysByYear(
        @RequestParam int year) {

        log.info("[req] GET /holidays?year=[] - 공휴일 데이터 조회 요청");

        return ResponseEntity.ok(
            ApiResponse.ok(
                holidayService.getHolidaysByYear(year).stream()
                    .map(HolidayResponse::from)
                    .toList()
            )
        );
    }

    @GetMapping(params = {"start-year", "end-year"})
    public ResponseEntity<ApiResponse<List<HolidayResponse>>> getHolidaysByYearRange(
        @RequestParam("start-year") int startYear,
        @RequestParam("end-year") int endYear) {

        log.info("[req] GET /holidays?start-year=[]]&end-year=[]] - 공휴일 데이터 조회(기간) 요청");

        return ResponseEntity.ok(
            ApiResponse.ok(
                holidayService.getHolidaysByYearRange(startYear, endYear).stream()
                    .map(HolidayResponse::from)
                    .toList()
            )
        );
    }

//    @GetMapping("/api/calendar/my-events")
//    public ResponseEntity<?> getMyEvents(@AuthenticationPrincipal Long userId) {
//        // 필터에서 저장했던 userId가 @AuthenticationPrincipal을 통해 자동으로 주입돼!
//        System.out.println("현재 로그인한 유저 ID: " + userId);
//
//        // DB에서 이 userId로 일정을 조회하면 끝!
//        return ResponseEntity.ok("...");
//    }
}

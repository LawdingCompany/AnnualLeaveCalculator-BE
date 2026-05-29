package com.lawding.leavecalc.domain.holiday.controller;

import com.lawding.leavecalc.domain.global.common.dto.response.ApiResponse;
import com.lawding.leavecalc.domain.holiday.dto.HolidayDto;
import com.lawding.leavecalc.domain.holiday.dto.response.HolidayResponse;
import com.lawding.leavecalc.domain.holiday.service.HolidayService;
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
}

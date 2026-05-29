package com.lawding.leavecalc.domain.holiday.service.impl;

import com.lawding.leavecalc.domain.holiday.dto.HolidayDto;
import com.lawding.leavecalc.domain.holiday.repository.HolidayRepository;
import com.lawding.leavecalc.domain.holiday.service.HolidayService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository repository;

    @Override
    public List<HolidayDto> getHolidaysByYear(int year) {
        return repository.findByYear(year).stream()
            .map(HolidayDto::from)
            .toList();
    }

    @Override
    public List<HolidayDto> getHolidaysByYearRange(int startYear, int endYear) {
        return repository.findByYearRange(startYear, endYear).stream()
            .map(HolidayDto::from)
            .toList();
    }
}

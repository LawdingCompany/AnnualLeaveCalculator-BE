package com.lawding.leavecalc.domain.holiday.service;

import com.lawding.leavecalc.domain.holiday.dto.HolidayDto;
import java.util.List;

public interface HolidayService {

    List<HolidayDto> getHolidaysByYear(int year);

    List<HolidayDto> getHolidaysByYearRange(int startYear, int endYear);
}

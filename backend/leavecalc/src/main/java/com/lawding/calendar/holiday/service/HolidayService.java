package com.lawding.calendar.holiday.service;

import com.lawding.calendar.holiday.dto.HolidayDto;
import java.util.List;

public interface HolidayService {

    List<HolidayDto> getHolidaysByYear(int year);

    List<HolidayDto> getHolidaysByYearRange(int startYear, int endYear);
}

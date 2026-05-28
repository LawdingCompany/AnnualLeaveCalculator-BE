package com.lawding.leavecalc.domain.holiday.service;

import com.lawding.leavecalc.domain.holiday.dto.HolidayDto;
import java.util.List;

public interface HolidayService {

    List<HolidayDto> getHolidays(int year);
}

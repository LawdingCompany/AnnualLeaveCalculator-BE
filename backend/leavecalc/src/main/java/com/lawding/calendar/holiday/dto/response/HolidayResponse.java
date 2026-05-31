package com.lawding.calendar.holiday.dto.response;

import com.lawding.calendar.holiday.dto.HolidayDto;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record HolidayResponse(
    LocalDate date,
    String name
) {

    public static HolidayResponse from(HolidayDto holiday) {
        return HolidayResponse.builder()
            .date(holiday.holidayDate())
            .name(holiday.holidayName())
            .build();
    }
}

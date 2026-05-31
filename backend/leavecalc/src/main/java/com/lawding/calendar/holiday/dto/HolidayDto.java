package com.lawding.calendar.holiday.dto;

import com.lawding.calendar.holiday.model.Holiday;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record HolidayDto(
    LocalDate holidayDate,
    String holidayName
) {

    public static HolidayDto from(Holiday holiday) {
        return HolidayDto.builder()
            .holidayDate(LocalDate.parse(holiday.getHolidayDate()))
            .holidayName(holiday.getHolidayName())
            .build();
    }
}

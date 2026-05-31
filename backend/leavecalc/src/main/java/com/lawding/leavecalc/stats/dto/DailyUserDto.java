package com.lawding.leavecalc.stats.dto;

import com.lawding.leavecalc.stats.model.DailyUser;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DailyUserDto(
    LocalDate recordDate,
    int web,
    int ios,
    int android
) {

    public static DailyUserDto from(DailyUser dailyUser) {
        return DailyUserDto.builder()
            .recordDate(LocalDate.parse(dailyUser.getRecordDate()))
            .web(dailyUser.getWeb())
            .ios(dailyUser.getIos())
            .android(dailyUser.getAndroid())
            .build();
    }
}

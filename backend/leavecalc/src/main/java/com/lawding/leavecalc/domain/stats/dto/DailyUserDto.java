package com.lawding.leavecalc.domain.stats.dto;

import com.lawding.leavecalc.domain.stats.entity.DailyUser;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DailyUserDto(
    LocalDate recordDate,
    int web,
    int ios,
    int android
) {

    public static DailyUserDto from(DailyUser entity) {
        return DailyUserDto.builder()
            .recordDate(entity.getRecordDate())
            .web(entity.getWeb())
            .ios(entity.getIos())
            .android(entity.getAndroid())
            .build();
    }
}

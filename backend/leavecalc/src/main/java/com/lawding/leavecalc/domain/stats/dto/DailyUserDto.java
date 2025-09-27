package com.lawding.leavecalc.domain.stats.dto;

import com.lawding.leavecalc.domain.stats.entity.DailyUser;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DailyUserDto (
    LocalDate date,
    int web,
    int ios
)
{
    public static DailyUserDto from(DailyUser entity){
        return DailyUserDto.builder()
            .date(entity.getDate())
            .web(entity.getWeb())
            .ios(entity.getIos())
            .build();
    }
}

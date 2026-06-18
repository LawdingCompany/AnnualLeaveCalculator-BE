package com.lawding.calendar.calendarevent.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record CalendarEventRequest(
    @NotBlank(message = "일정 제목은 필수입니다.")
    String title,
    String description,
    @NotNull(message = "일정 시작 시간은 필수입니다.")
    LocalDateTime startDatetime,
    @NotNull(message = "일정 종료 시간은 필수입니다.")
    LocalDateTime endDatetime,
    @PositiveOrZero(message = "사용 연차 시간은 0 이상이어야 합니다.")
    Integer usedLeaveMinutes,
    Boolean isAllDay,
    Boolean isLeaveEvent
) {

}

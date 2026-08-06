package com.lawding.calendar.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateRemainingLeaveMinutesRequest(
    @NotNull(message = "남은 연차 시간은 필수입니다.")
    @PositiveOrZero(message = "남은 연차 시간은 0분 이상이어야 합니다.")
    Integer remainingLeaveMinutes
) {
}

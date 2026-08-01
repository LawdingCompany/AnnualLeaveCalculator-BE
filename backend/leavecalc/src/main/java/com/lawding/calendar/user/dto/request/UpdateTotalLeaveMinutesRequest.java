package com.lawding.calendar.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateTotalLeaveMinutesRequest(
    @NotNull
    @PositiveOrZero
    Integer totalLeaveMinutes
) {
}

package com.lawding.calendar.recommendation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RecommendedScheduleRequest(
    @NotBlank @Size(max = 100) String name,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate
) {
}

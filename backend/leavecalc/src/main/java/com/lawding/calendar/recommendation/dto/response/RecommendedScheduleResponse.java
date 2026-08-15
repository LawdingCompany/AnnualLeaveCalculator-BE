package com.lawding.calendar.recommendation.dto.response;

import com.lawding.calendar.recommendation.entity.RecommendedSchedule;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecommendedScheduleResponse(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static RecommendedScheduleResponse from(RecommendedSchedule schedule) {
        return new RecommendedScheduleResponse(
            schedule.getId(),
            schedule.getName(),
            schedule.getStartDate(),
            schedule.getEndDate(),
            schedule.getCreatedAt(),
            schedule.getUpdatedAt()
        );
    }
}

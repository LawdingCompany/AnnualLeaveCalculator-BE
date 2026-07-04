package com.lawding.calendar.user.dto.response;

import com.lawding.calendar.calendarevent.entity.CalendarEvent;
import java.time.LocalDateTime;

public record RecentLeaveUsageResponse(
    LocalDateTime startDatetime,
    LocalDateTime endDatetime,
    Integer usedLeaveMinutes
) {

    public static RecentLeaveUsageResponse from(CalendarEvent event) {
        return new RecentLeaveUsageResponse(
            event.getStartDatetime(),
            event.getEndDatetime(),
            event.getUsedLeaveMinutes()
        );
    }
}

package com.lawding.calendar.calendarevent.dto.request;

import java.time.LocalDateTime;

public record CalendarEventRequest(
    String title,
    String description,
    LocalDateTime startDatetime,
    LocalDateTime endDatetime,
    Integer usedLeaveMinutes,
    Boolean isAllDay,
    Boolean isLeaveEvent
) {

}

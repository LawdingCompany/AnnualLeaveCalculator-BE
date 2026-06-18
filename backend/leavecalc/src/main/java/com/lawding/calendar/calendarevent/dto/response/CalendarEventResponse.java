package com.lawding.calendar.calendarevent.dto.response;

import com.lawding.calendar.calendarevent.entity.CalendarEvent;
import java.time.LocalDateTime;

public record CalendarEventResponse(
    Long id,
    String title,
    String description,
    LocalDateTime startDatetime,
    LocalDateTime endDatetime,
    Integer usedLeaveMinutes,
    Boolean isAllDay,
    Boolean isLeaveEvent
) {

    public static CalendarEventResponse from(CalendarEvent event) {

        return new CalendarEventResponse(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            event.getStartDatetime(),
            event.getEndDatetime(),
            event.getUsedLeaveMinutes(),
            event.getIsAllDay(),
            event.getIsLeaveEvent()
        );
    }
}

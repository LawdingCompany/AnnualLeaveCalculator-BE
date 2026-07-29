package com.lawding.calendar.calendarevent.dto.response;

import com.lawding.calendar.calendarevent.entity.CalendarEvent;

public record CalendarEventCreateResponse(Long id, String title) {

    public static CalendarEventCreateResponse from(CalendarEvent event) {
        return new CalendarEventCreateResponse(event.getId(), event.getTitle());
    }
}

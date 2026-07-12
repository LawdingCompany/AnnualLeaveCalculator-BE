package com.lawding.calendar.calendarevent.service;

import com.lawding.calendar.calendarevent.dto.request.CalendarEventRequest;
import com.lawding.calendar.calendarevent.entity.CalendarEvent;
import java.util.List;

public interface CalendarEventService {

    CalendarEvent createEvent(Long userId, CalendarEventRequest request);

    List<CalendarEvent> findEventsByMonth(Long userId, int year, int month);

    CalendarEvent findEvent(Long userId, Long eventId);

    void updateEvent(Long userId, Long eventId, CalendarEventRequest request);

    void deleteEvent(Long userId, Long eventId);
}

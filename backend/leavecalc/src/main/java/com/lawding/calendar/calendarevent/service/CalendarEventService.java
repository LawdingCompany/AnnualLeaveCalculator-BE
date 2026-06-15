package com.lawding.calendar.calendarevent.service;

import com.lawding.calendar.calendarevent.dto.request.CalendarEventRequest;

public interface CalendarEventService {

    void createEvent(Long userId, CalendarEventRequest request);
}

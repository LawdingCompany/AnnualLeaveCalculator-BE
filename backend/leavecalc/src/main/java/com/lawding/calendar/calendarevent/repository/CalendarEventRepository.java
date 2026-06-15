package com.lawding.calendar.calendarevent.repository;

import com.lawding.calendar.calendarevent.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

}

package com.lawding.calendar.calendarevent.repository;

import com.lawding.calendar.calendarevent.entity.CalendarEventLeaveAllocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEventLeaveAllocationRepository extends JpaRepository<CalendarEventLeaveAllocation, Long> {
    List<CalendarEventLeaveAllocation> findAllByEvent_IdOrderByIdAsc(Long eventId);
    void deleteAllByEvent_Id(Long eventId);
    void deleteByEvent_User_Id(Long userId);
}

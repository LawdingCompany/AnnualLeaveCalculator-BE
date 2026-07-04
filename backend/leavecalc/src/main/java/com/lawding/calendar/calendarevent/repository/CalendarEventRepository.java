package com.lawding.calendar.calendarevent.repository;

import com.lawding.calendar.calendarevent.entity.CalendarEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findAllByUser_IdAndStartDatetimeLessThanAndEndDatetimeGreaterThanEqualOrderByStartDatetimeAsc(
        Long userId,
        LocalDateTime monthEndExclusive,
        LocalDateTime monthStart
    );

    Optional<CalendarEvent> findByIdAndUser_Id(Long id, Long userId);

    void deleteByUser_Id(Long userId);

    List<CalendarEvent> findAllByUser_IdAndIsLeaveEventTrueAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualOrderByStartDatetimeDesc(
        Long userId,
        LocalDateTime periodEnd,
        LocalDateTime periodStart
    );
}

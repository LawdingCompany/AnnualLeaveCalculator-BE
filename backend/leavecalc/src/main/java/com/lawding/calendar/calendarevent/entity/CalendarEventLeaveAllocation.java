package com.lawding.calendar.calendarevent.entity;

import com.lawding.calendar.user.entity.LeaveGrant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "calendar_event_leave_allocations")
public class CalendarEventLeaveAllocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "calendar_event_id", nullable = false)
    private CalendarEvent event;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "leave_grant_id", nullable = false)
    private LeaveGrant grant;
    @Column(nullable = false)
    private Integer allocatedMinutes;

    private CalendarEventLeaveAllocation(CalendarEvent event, LeaveGrant grant, int allocatedMinutes) {
        this.event = event;
        this.grant = grant;
        this.allocatedMinutes = allocatedMinutes;
    }

    public static CalendarEventLeaveAllocation create(CalendarEvent event, LeaveGrant grant, int minutes) {
        return new CalendarEventLeaveAllocation(event, grant, minutes);
    }
}

package com.lawding.calendar.user.entity;

import com.lawding.auth.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "calendar_events")
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // 사용자 식별자(FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_yearly_balance_id")
    private LeaveYearlyBalance leaveYearlyBalance;  // 연차 정산 정보 식별자(FK)

    private String title;   // 일정 제목

    @Column(columnDefinition = "TEXT")
    private String description; // 일정 메모

    private LocalDateTime startDatetime;    // 일정 시작 시각
    private LocalDateTime endDatetime;  // 일정 종료 시각

    private Integer usedLeaveMinutes;   // 사용된 연차 시간(분)

    private Boolean isAllDay;   // 종일 일정 여부

    private Boolean isLeaveEvent;   // 연차 일정 여부

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public CalendarEvent(User user, LeaveYearlyBalance leaveYearlyBalance, String title,
        String description, LocalDateTime startDatetime, LocalDateTime endDatetime,
        Integer usedLeaveMinutes, Boolean isAllDay, Boolean isLeaveEvent) {
        this.user = user;
        this.leaveYearlyBalance = leaveYearlyBalance;
        this.title = title;
        this.description = description;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.usedLeaveMinutes = usedLeaveMinutes;
        this.isAllDay = isAllDay;
        this.isLeaveEvent = isLeaveEvent;
    }

    public void update(String title, String description, LocalDateTime startDatetime,
        LocalDateTime endDatetime, Integer usedLeaveMinutes, Boolean isAllDay) {
        this.title = title;
        this.description = description;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.usedLeaveMinutes = usedLeaveMinutes;
        this.isAllDay = isAllDay;
    }

}

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
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "leave_yearly_balances")
public class LeaveYearlyBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate startDate;    // 연차 사용 기간 시작일
    private LocalDate endDate;      // 연차 사용 기간 종료일

    private Integer weeklyWorkingDays;  // 주당 근무 일수

    @Column(precision = 4, scale = 2)
    private BigDecimal avgDailyWorkHours;   // 일 평균 근무 시간

    private Integer totalLeaveMinutes;  // 발생한 총 연차 시간(분)

    @Column(nullable = false)
    private Integer usedLeaveMinutes;   // 사용한 연차 시간(분)

    private Boolean isFinalized;    // 해당 기간 연차 마감 여부

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public LeaveYearlyBalance(User user, LocalDate startDate, LocalDate endDate,
        Integer weeklyWorkingDays, BigDecimal avgDailyWorkHours,
        Integer totalLeaveMinutes) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weeklyWorkingDays = weeklyWorkingDays;
        this.avgDailyWorkHours = avgDailyWorkHours;
        this.totalLeaveMinutes = totalLeaveMinutes;
        this.usedLeaveMinutes = 0;
        this.isFinalized = false;
    }

    public void deductLeave(int minutes) {
        if (this.isFinalized) {
            throw new IllegalStateException("이미 마감된 연차 기간입니다.");
        }
        if (this.usedLeaveMinutes + minutes > this.totalLeaveMinutes) {
            throw new IllegalArgumentException("잔여 연차 시간을 초과하여 사용할 수 없습니다.");
        }
        this.usedLeaveMinutes += minutes;
    }

    public void finalizeBalance() {
        this.isFinalized = true;
    }

    public int getRemainingMinutes() {
        return this.totalLeaveMinutes - this.usedLeaveMinutes;
    }

    /**
     * 주어진 날짜가 이 연차 정산 기간에 포함되는지 확인
     */
    public boolean containsDate(LocalDate date) {
        return !date.isBefore(this.startDate) && !date.isAfter(this.endDate);
    }
}

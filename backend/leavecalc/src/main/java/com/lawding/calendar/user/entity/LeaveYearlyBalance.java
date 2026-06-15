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

    private Boolean isFinalized = false;    // 해당 기간 연차 마감 여부

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public LeaveYearlyBalance(User user, LocalDate startDate, LocalDate endDate,
        Integer weeklyWorkingDays, BigDecimal avgDailyWorkHours,
        Integer totalLeaveMinutes, Integer usedLeaveMinutes) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weeklyWorkingDays = weeklyWorkingDays;
        this.avgDailyWorkHours = avgDailyWorkHours;
        this.totalLeaveMinutes = totalLeaveMinutes;
        this.usedLeaveMinutes = usedLeaveMinutes;
    }

    public static LeaveYearlyBalance create(User user, LocalDate startDate, LocalDate endDate,
        Integer weeklyWorkingDays, BigDecimal avgDailyWorkHours,
        Integer totalLeaveMinutes, Integer usedLeaveMinutes) {
        return LeaveYearlyBalance.builder()
            .user(user)
            .startDate(startDate)
            .endDate(endDate)
            .weeklyWorkingDays(weeklyWorkingDays)
            .avgDailyWorkHours(avgDailyWorkHours)
            .totalLeaveMinutes(totalLeaveMinutes)
            .usedLeaveMinutes(usedLeaveMinutes)
            .build();
    }

    public void useLeave(int minutes) {
        if (this.isFinalized) {
            throw new IllegalStateException("이미 마감된 연차 기간입니다.");
        }
        int remainingMinutes = getRemainingMinutes();
        if (minutes > remainingMinutes) {
            throw new IllegalArgumentException("잔여 연차가 부족합니다.");
        }
        this.usedLeaveMinutes += minutes;
    }

    public void finalizeBalance() {
        this.isFinalized = true;
    }

    public int getRemainingMinutes() {
        return this.totalLeaveMinutes - this.usedLeaveMinutes;
    }

}

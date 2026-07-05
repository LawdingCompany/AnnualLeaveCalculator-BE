package com.lawding.calendar.user.dto.response;

import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LeaveYearlyBalanceResponse(
    Long id,
    Long userId,
    LocalDate startDate,
    LocalDate endDate,
    Integer weeklyWorkingDays,
    BigDecimal avgDailyWorkHours,
    Integer totalLeaveMinutes,
    Integer usedLeaveMinutes,
    Integer remainingLeaveMinutes,
    Boolean isFinalized
) {

    public static LeaveYearlyBalanceResponse from(LeaveYearlyBalance balance) {
        return new LeaveYearlyBalanceResponse(
            balance.getId(),
            balance.getUser().getId(),
            balance.getStartDate(),
            balance.getEndDate(),
            balance.getWeeklyWorkingDays(),
            balance.getAvgDailyWorkHours(),
            balance.getTotalLeaveMinutes(),
            balance.getUsedLeaveMinutes(),
            balance.getRemainingMinutes(),
            balance.getIsFinalized()
        );
    }
}

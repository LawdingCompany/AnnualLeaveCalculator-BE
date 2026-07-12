package com.lawding.calendar.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record LeaveDashboardResponse(
    Integer availableLeaveMinutes,
    BigDecimal avgDailyWorkHours,
    Integer totalLeaveMinutes,
    Integer leaveAccrualBasis,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer fiscalYearBaseMonth,
    LocalDate nextLeaveAccrualDate,
    Integer expiringLeaveMinutes,
    LocalDate leavePeriodStartDate,
    LocalDate leavePeriodEndDate,
    List<RecentLeaveUsageResponse> recentLeaveUsages
) {
}

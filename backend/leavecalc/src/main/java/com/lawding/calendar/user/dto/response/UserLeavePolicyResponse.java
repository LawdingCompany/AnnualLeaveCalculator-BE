package com.lawding.calendar.user.dto.response;

import com.lawding.calendar.user.dto.WorkPattern;
import com.lawding.calendar.user.entity.UserLeavePolicy;
import com.lawding.calendar.user.enums.LeaveUsageType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserLeavePolicyResponse(
    Long userId,
    LocalDateTime acceptedAt,
    Integer leaveAccrualBasis,
    LocalDate hireDate,
    Integer fiscalYearBaseMonth,
    Integer companySize,
    WorkPattern workPattern,
    WorkPattern breakTimePattern,
    LeaveUsageType leaveUsageType
) {

    public static UserLeavePolicyResponse from(UserLeavePolicy policy) {
        return new UserLeavePolicyResponse(
            policy.getUserId(),
            policy.getAcceptedAt(),
            policy.getLeaveAccrualBasis().getCode(),
            policy.getHireDate(),
            policy.getFiscalYearBaseMonth(),
            policy.getCompanySize(),
            policy.getWorkPattern(),
            policy.getBreakTimePattern(),
            policy.getLeaveUsageType()
        );
    }
}

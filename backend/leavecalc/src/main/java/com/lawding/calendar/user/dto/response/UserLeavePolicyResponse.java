package com.lawding.calendar.user.dto.response;

import com.lawding.calendar.user.dto.WorkPattern;
import com.lawding.calendar.user.entity.UserLeavePolicy;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserLeavePolicyResponse(
    Long userId,
    LocalDateTime acceptedAt,
    Integer leaveAccrualBasis,
    String leaveAccrualBasisName,
    LocalDate hireDate,
    Integer fiscalYearBaseMonth,
    Integer companySize,
    WorkPattern workPattern,
    WorkPattern breakTimePattern,
    LocalDate nextLeaveAccrualDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static UserLeavePolicyResponse from(UserLeavePolicy policy) {
        return new UserLeavePolicyResponse(
            policy.getUserId(),
            policy.getAcceptedAt(),
            policy.getLeaveAccrualBasis().getCode(),
            policy.getLeaveAccrualBasis().name(),
            policy.getHireDate(),
            policy.getFiscalYearBaseMonth(),
            policy.getCompanySize(),
            policy.getWorkPattern(),
            policy.getBreakTimePattern(),
            policy.getNextLeaveAccrualDate(),
            policy.getCreatedAt(),
            policy.getUpdatedAt()
        );
    }
}

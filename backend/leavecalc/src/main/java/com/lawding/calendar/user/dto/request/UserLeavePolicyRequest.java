package com.lawding.calendar.user.dto.request;

import com.lawding.calendar.user.dto.WorkPattern;
import java.time.LocalDate;

public record UserLeavePolicyRequest(
    String nickname,
    Boolean acceptedTerms,
    Integer leaveAccrualBasis,
    Integer fiscalYearBaseMonth,
    LocalDate hireDate,
    WorkPattern workPattern,
    WorkPattern breakTimePattern,
    Integer companySize,
    Integer totalLeave,
    Integer usedLeave
) {
}

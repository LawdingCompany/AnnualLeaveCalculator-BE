package com.lawding.calendar.user.dto.request;

import com.lawding.calendar.user.dto.WorkPattern;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
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
    @DecimalMin(value = "0.000", message = "totalLeave는 0 이상이어야 합니다.")
    @Digits(integer = 6, fraction = 3, message = "totalLeave는 소수점 셋째 자리까지만 입력할 수 있습니다.")
    BigDecimal totalLeave,
    @DecimalMin(value = "0.000", message = "usedLeave는 0 이상이어야 합니다.")
    @Digits(integer = 6, fraction = 3, message = "usedLeave는 소수점 셋째 자리까지만 입력할 수 있습니다.")
    BigDecimal usedLeave
) {
}

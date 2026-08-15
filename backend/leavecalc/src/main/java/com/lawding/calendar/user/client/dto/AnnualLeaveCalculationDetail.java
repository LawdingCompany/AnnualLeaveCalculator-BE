package com.lawding.calendar.user.client.dto;

import java.math.BigDecimal;

public record AnnualLeaveCalculationDetail(
    LambdaDatePeriod accrualPeriod,
    LambdaDatePeriod availablePeriod,
    BigDecimal totalLeaveDays,
    AnnualLeaveCalculationDetail monthlyDetail,
    AnnualLeaveCalculationDetail proratedDetail
) {}

package com.lawding.calendar.user.client.dto;

public record AnnualLeaveCalculationResponse(
    String calculationId,
    String calculationType,
    String fiscalYear,
    String hireDate,
    String referenceDate,
    String leaveType,
    AnnualLeaveCalculationDetail calculationDetail
) {}

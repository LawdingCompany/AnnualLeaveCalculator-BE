package com.lawding.calendar.user.client.dto;

import java.time.LocalDate;
import java.util.List;

public record AnnualLeaveCalculationRequest(
    int calculationType,
    String fiscalYear,
    LocalDate hireDate,
    LocalDate referenceDate,
    List<Object> nonWorkingPeriods,
    List<LocalDate> companyHolidays
) {}

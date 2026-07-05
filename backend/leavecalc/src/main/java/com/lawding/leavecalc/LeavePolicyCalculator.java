package com.lawding.leavecalc;

import static java.time.temporal.ChronoUnit.YEARS;

import com.lawding.calendar.user.dto.WorkPattern;
import com.lawding.calendar.user.enums.LeaveAccrualBasis;
import com.lawding.global.common.dto.DatePeriod;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class LeavePolicyCalculator {

    public DatePeriod calculateCurrentPeriod(
        LeaveAccrualBasis basis,
        LocalDate hireDate,
        Integer fiscalYearBaseMonth,
        LocalDate referenceDate
    ) {
        LocalDate startDate = switch (basis) {
            case HIRE_DATE -> calculateHireStart(hireDate, referenceDate);
            case FISCAL_YEAR -> calculateFiscalStart(hireDate, fiscalYearBaseMonth, referenceDate);
        };
        return new DatePeriod(startDate, startDate.plusYears(1).minusDays(1));
    }

    private LocalDate calculateHireStart(LocalDate hireDate, LocalDate now) {
        long years = YEARS.between(hireDate, now);
        return hireDate.plusYears(years);
    }

    private LocalDate calculateFiscalStart(
        LocalDate hireDate,
        Integer baseMonth,
        LocalDate referenceDate
    ) {
        LocalDate fiscalStart = LocalDate.of(referenceDate.getYear(), baseMonth, 1);
        if (hireDate.isBefore(fiscalStart)) {
            fiscalStart = fiscalStart.minusYears(1);
        }
        return fiscalStart;
    }

    public int calculateWeeklyWorkingDays(WorkPattern workPattern) {
        return workPattern.weeklyWorkingDays();
    }

    public BigDecimal calculateAvgDailyWorkHours(WorkPattern workPattern) {
        return calculateAvgDailyWorkHours(workPattern, null);
    }

    public BigDecimal calculateAvgDailyWorkHours(WorkPattern workPattern, WorkPattern breakTimePattern) {
        long totalWeeklyMinutes = workPattern.totalWeeklyMinutes();
        long totalBreakMinutes = breakTimePattern == null ? 0 : breakTimePattern.totalWeeklyMinutes();
        long netWeeklyMinutes = Math.max(0, totalWeeklyMinutes - totalBreakMinutes);
        int workingDays = workPattern.weeklyWorkingDays();

        if (workingDays == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(netWeeklyMinutes)
            .divide(BigDecimal.valueOf(workingDays), 2, RoundingMode.HALF_UP)
            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    public int convertLeaveDaysToMinutes(
        BigDecimal leaveDays,
        BigDecimal avgDailyWorkHours
    ) {
        if (leaveDays == null || avgDailyWorkHours == null) {
            return 0;
        }

        return leaveDays
            .multiply(avgDailyWorkHours)
            .multiply(BigDecimal.valueOf(60))
            .intValue();
    }
}

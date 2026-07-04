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

    /**
     * =========================
     * 1. 다음 연차 발생일 계산
     * =========================
     *
     * - HIRE_DATE: 입사일 기준 +1년
     * - FISCAL_YEAR: 회계연도 기준 다음 시작일
     */
    public LocalDate calculateNextLeaveAccrualDate(
        LeaveAccrualBasis basis,
        LocalDate hireDate,
        Integer fiscalYearBaseMonth
    ) {
        return switch (basis) {
            case HIRE_DATE -> hireDate.plusYears(1);

            case FISCAL_YEAR -> calculateFiscalStart(hireDate, fiscalYearBaseMonth, hireDate)
                .plusYears(1);
        };
    }

    /**
     * =========================
     * 2. 현재 연차 정산 기간
     * =========================
     *
     * return:
     * - startDate ~ endDate (1년 구간)
     */
    public DatePeriod calculateCurrentPeriod(
        LeaveAccrualBasis basis,
        LocalDate hireDate,
        Integer fiscalYearBaseMonth,
        LocalDate referenceDate
    ) {

        LocalDate startDate = switch (basis) {
            // 입사일 기준: 경과 연수만큼 이동
            case HIRE_DATE -> calculateHireStart(hireDate, referenceDate);
            // 회계연도 기준: 현재 기준 회계 시작일
            case FISCAL_YEAR -> calculateFiscalStart(hireDate, fiscalYearBaseMonth, referenceDate);
        };

        // 연차 기간은 항상 1년 단위
        LocalDate endDate = startDate.plusYears(1).minusDays(1);

        return new DatePeriod(startDate, endDate);
    }

    /**
     * =========================
     * 3. HIRE_DATE 기준 시작일
     * =========================
     *
     * 예:
     * hireDate = 2024-03-01
     * now      = 2025-06-03
     * → 2025-03-01
     */
    private LocalDate calculateHireStart(LocalDate hireDate, LocalDate now) {

        long years = YEARS.between(hireDate, now);

        return hireDate.plusYears(years);
    }

    /**
     * =========================
     * 4. FISCAL_YEAR 기준 시작일
     * =========================
     *
     * 규칙:
     * - 기준 연도 + baseMonth 1일 생성
     * - 입사일이 그 이전이면 → 전년도 기준으로 보정
     */
    private LocalDate calculateFiscalStart(
        LocalDate hireDate,
        Integer baseMonth,
        LocalDate referenceDate
    ) {

        LocalDate fiscalStart =
            LocalDate.of(referenceDate.getYear(), baseMonth, 1);

        if (hireDate.isBefore(fiscalStart)) {
            fiscalStart = fiscalStart.minusYears(1);
        }

        return fiscalStart;
    }

    /**
     * =========================
     * 5. 주당 근무일수 계산
     * =========================
     */
    public int calculateWeeklyWorkingDays(WorkPattern workPattern) {
        return workPattern.weeklyWorkingDays();
    }

    /**
     * =========================
     * 6. 일 평균 근무시간 계산
     * =========================
     * weekly total minutes / 근무일수 → 하루 평균 근무시간(시간 단위) : 소수점 둘째자리까지 반올림
     */
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

    /**
     * =========================
     * 연차(일 단위) → 분(minute) 변환 공통 함수
     * =========================
     *
     * 공통 공식:
     * days × avgDailyWorkHours × 60
     */
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

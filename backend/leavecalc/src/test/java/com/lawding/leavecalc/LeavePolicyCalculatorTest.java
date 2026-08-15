package com.lawding.leavecalc;

import static org.assertj.core.api.Assertions.assertThat;

import com.lawding.calendar.user.enums.LeaveAccrualBasis;
import com.lawding.global.common.dto.DatePeriod;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LeavePolicyCalculatorTest {

    private final LeavePolicyCalculator calculator = new LeavePolicyCalculator();

    @Test
    void calculatesFiscalPeriodContainingReferenceDateWhenBaseMonthHasPassed() {
        DatePeriod period = calculator.calculateCurrentPeriod(
            LeaveAccrualBasis.FISCAL_YEAR,
            LocalDate.of(2020, 3, 1),
            1,
            LocalDate.of(2026, 8, 15)
        );

        assertThat(period.startDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(period.endDate()).isEqualTo(LocalDate.of(2026, 12, 31));
    }

    @Test
    void calculatesPreviousYearStartWhenReferenceDateIsBeforeBaseMonth() {
        DatePeriod period = calculator.calculateCurrentPeriod(
            LeaveAccrualBasis.FISCAL_YEAR,
            LocalDate.of(2020, 3, 1),
            10,
            LocalDate.of(2026, 8, 15)
        );

        assertThat(period.startDate()).isEqualTo(LocalDate.of(2025, 10, 1));
        assertThat(period.endDate()).isEqualTo(LocalDate.of(2026, 9, 30));
    }

    @Test
    void fiscalPeriodDoesNotDependOnHireDate() {
        DatePeriod longTermEmployeePeriod = calculator.calculateCurrentPeriod(
            LeaveAccrualBasis.FISCAL_YEAR,
            LocalDate.of(2020, 3, 1),
            1,
            LocalDate.of(2026, 8, 15)
        );
        DatePeriod recentEmployeePeriod = calculator.calculateCurrentPeriod(
            LeaveAccrualBasis.FISCAL_YEAR,
            LocalDate.of(2026, 3, 1),
            1,
            LocalDate.of(2026, 8, 15)
        );

        assertThat(recentEmployeePeriod).isEqualTo(longTermEmployeePeriod);
    }

    @Test
    void keepsHireDateAnniversaryPeriodForHireDateBasis() {
        DatePeriod period = calculator.calculateCurrentPeriod(
            LeaveAccrualBasis.HIRE_DATE,
            LocalDate.of(2020, 3, 1),
            null,
            LocalDate.of(2026, 8, 15)
        );

        assertThat(period.startDate()).isEqualTo(LocalDate.of(2026, 3, 1));
        assertThat(period.endDate()).isEqualTo(LocalDate.of(2027, 2, 28));
    }
}

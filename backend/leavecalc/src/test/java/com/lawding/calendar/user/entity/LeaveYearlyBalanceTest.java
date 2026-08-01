package com.lawding.calendar.user.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lawding.global.exception.ClientException;
import com.lawding.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LeaveYearlyBalanceTest {

    @Test
    void updatesTotalAndRecalculatesRemainingMinutes() {
        LeaveYearlyBalance balance = balance(7_200, 6_240);

        balance.updateTotalLeaveMinutes(8_160, 6_240);

        assertThat(balance.getTotalLeaveMinutes()).isEqualTo(8_160);
        assertThat(balance.getRemainingMinutes()).isEqualTo(1_920);
    }

    @Test
    void rejectsTotalLowerThanPersistedUsedMinutes() {
        LeaveYearlyBalance balance = balance(7_200, 6_240);

        assertThatThrownBy(() -> balance.updateTotalLeaveMinutes(5_760, 5_000))
            .isInstanceOf(ClientException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.LEAVE_TOTAL_LESS_THAN_USED);
    }

    @Test
    void rejectsTotalLowerThanScheduledUsedMinutes() {
        LeaveYearlyBalance balance = balance(7_200, 5_000);

        assertThatThrownBy(() -> balance.updateTotalLeaveMinutes(5_760, 6_240))
            .isInstanceOf(ClientException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.LEAVE_TOTAL_LESS_THAN_USED);
    }

    private LeaveYearlyBalance balance(int totalMinutes, int usedMinutes) {
        return LeaveYearlyBalance.create(
            null,
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31),
            5,
            BigDecimal.valueOf(8),
            totalMinutes,
            usedMinutes
        );
    }
}

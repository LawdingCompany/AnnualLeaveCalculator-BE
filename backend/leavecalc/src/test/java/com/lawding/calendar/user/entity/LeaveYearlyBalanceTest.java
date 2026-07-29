package com.lawding.calendar.user.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lawding.global.exception.ClientException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LeaveYearlyBalanceTest {

    @Test
    void updatesOnlyTotalLeaveMinutesAndRecalculatesRemainingMinutes() {
        LeaveYearlyBalance balance = createBalance(7_200, 960);

        balance.updateTotalLeaveMinutes(8_160);

        assertThat(balance.getTotalLeaveMinutes()).isEqualTo(8_160);
        assertThat(balance.getUsedLeaveMinutes()).isEqualTo(960);
        assertThat(balance.getRemainingLeaveMinutes()).isEqualTo(7_200);
    }

    @Test
    void rejectsTotalLeaveMinutesLessThanUsedMinutes() {
        LeaveYearlyBalance balance = createBalance(7_200, 960);

        assertThatThrownBy(() -> balance.updateTotalLeaveMinutes(959))
            .isInstanceOf(ClientException.class);
        assertThat(balance.getTotalLeaveMinutes()).isEqualTo(7_200);
    }

    @Test
    void rejectsUpdatingFinalizedBalance() {
        LeaveYearlyBalance balance = createBalance(7_200, 960);
        balance.finalizeBalance();

        assertThatThrownBy(() -> balance.updateTotalLeaveMinutes(8_160))
            .isInstanceOf(ClientException.class);
        assertThat(balance.getTotalLeaveMinutes()).isEqualTo(7_200);
    }

    private LeaveYearlyBalance createBalance(int totalMinutes, int usedMinutes) {
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

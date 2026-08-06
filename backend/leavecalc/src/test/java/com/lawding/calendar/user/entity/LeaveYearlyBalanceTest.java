package com.lawding.calendar.user.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lawding.global.exception.ClientException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LeaveYearlyBalanceTest {

    @Test
    void updatesRemainingMinutesByRecalculatingTotalMinutes() {
        LeaveYearlyBalance balance = createBalance(7_200, 3_840);

        balance.updateRemainingLeaveMinutes(4_320);

        assertThat(balance.getTotalLeaveMinutes()).isEqualTo(8_160);
        assertThat(balance.getUsedLeaveMinutes()).isEqualTo(3_840);
        assertThat(balance.getRemainingLeaveMinutes()).isEqualTo(4_320);
    }

    @Test
    void updatesTotalToRemainingWhenNoLeaveHasBeenUsed() {
        LeaveYearlyBalance balance = createBalance(7_680, 0);

        balance.updateRemainingLeaveMinutes(7_200);

        assertThat(balance.getTotalLeaveMinutes()).isEqualTo(7_200);
        assertThat(balance.getRemainingLeaveMinutes()).isEqualTo(7_200);
    }

    @Test
    void allowsZeroRemainingMinutes() {
        LeaveYearlyBalance balance = createBalance(7_200, 3_840);

        balance.updateRemainingLeaveMinutes(0);

        assertThat(balance.getTotalLeaveMinutes()).isEqualTo(3_840);
        assertThat(balance.getRemainingLeaveMinutes()).isZero();
    }

    @Test
    void rejectsNegativeRemainingMinutes() {
        LeaveYearlyBalance balance = createBalance(7_200, 960);

        assertThatThrownBy(() -> balance.updateRemainingLeaveMinutes(-1))
            .isInstanceOf(ClientException.class);
        assertThat(balance.getTotalLeaveMinutes()).isEqualTo(7_200);
    }

    @Test
    void rejectsUpdatingFinalizedBalance() {
        LeaveYearlyBalance balance = createBalance(7_200, 960);
        balance.finalizeBalance();

        assertThatThrownBy(() -> balance.updateRemainingLeaveMinutes(6_720))
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

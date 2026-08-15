package com.lawding.calendar.user.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lawding.auth.entity.User;
import com.lawding.calendar.user.enums.LeaveGrantSource;
import com.lawding.calendar.user.enums.LeaveGrantType;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LeaveGrantTest {
    @Test
    void tracksUseCancellationAndAdjustment() {
        LeaveGrant grant = createGrant(480, 0);

        grant.use(120);
        grant.adjust(60);
        assertThat(grant.getRemainingMinutes()).isEqualTo(420);

        grant.cancel(120);
        assertThat(grant.getRemainingMinutes()).isEqualTo(540);
    }

    @Test
    void cannotReduceBelowAlreadyAvailableAmount() {
        LeaveGrant grant = createGrant(480, 120);
        assertThatThrownBy(() -> grant.adjust(-361))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private LeaveGrant createGrant(int granted, int used) {
        User user = User.builder().username("u").email("u@test.com").provider("TEST").build();
        LeaveYearlyBalance balance = LeaveYearlyBalance.create(user,
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 5,
            BigDecimal.valueOf(8), granted, used);
        return LeaveGrant.create(user, balance, LeaveGrantType.MONTHLY,
            LeaveGrantSource.MONTHLY_BATCH, "MONTHLY:1", granted, used,
            LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 12, 31));
    }
}

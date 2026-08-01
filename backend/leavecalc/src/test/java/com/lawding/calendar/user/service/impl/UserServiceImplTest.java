package com.lawding.calendar.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.lawding.auth.entity.User;
import com.lawding.auth.repository.AuthRepository;
import com.lawding.calendar.calendarevent.repository.CalendarEventRepository;
import com.lawding.calendar.user.dto.WorkPattern;
import com.lawding.calendar.user.dto.response.UserContextResponse;
import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.entity.UserLeavePolicy;
import com.lawding.calendar.user.enums.LeaveAccrualBasis;
import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import com.lawding.calendar.user.repository.UserLeavePolicyRepository;
import com.lawding.leavecalc.LeavePolicyCalculator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private LeavePolicyCalculator leavePolicyCalculator;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private UserLeavePolicyRepository userLeavePolicyRepository;

    @Mock
    private LeaveYearlyBalanceRepository leaveYearlyBalanceRepository;

    @Mock
    private CalendarEventRepository calendarEventRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void returnsUserPolicyAndCurrentLeaveBalanceTogether() {
        Long userId = 1L;
        User user = createUser();
        user.completeOnboarding();
        UserLeavePolicy policy = UserLeavePolicy.create(
            user,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LeaveAccrualBasis.HIRE_DATE,
            LocalDate.of(2024, 3, 1),
            null,
            5,
            new WorkPattern(Map.of()),
            new WorkPattern(Map.of())
        );
        LeaveYearlyBalance balance = LeaveYearlyBalance.create(
            user,
            LocalDate.of(2026, 3, 1),
            LocalDate.of(2027, 2, 28),
            5,
            BigDecimal.valueOf(8),
            8_160,
            960
        );

        when(authRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userLeavePolicyRepository.findById(userId)).thenReturn(Optional.of(policy));
        when(leaveYearlyBalanceRepository.findCurrentBalance(any(Long.class), any(LocalDate.class)))
            .thenReturn(balance);

        UserContextResponse response = userService.getUserContext(userId);

        assertThat(response.user().nickname()).isEqualTo("lawding");
        assertThat(response.user().onboardingCompleted()).isTrue();
        assertThat(response.leavePolicy().leaveAccrualBasis()).isEqualTo(1);
        assertThat(response.leaveBalance().totalLeaveMinutes()).isEqualTo(8_160);
        assertThat(response.leaveBalance().usedLeaveMinutes()).isEqualTo(960);
        assertThat(response.leaveBalance().remainingLeaveMinutes()).isEqualTo(7_200);
    }

    @Test
    void returnsNullPolicyAndBalanceForUserBeforeOnboarding() {
        Long userId = 1L;
        User user = createUser();

        when(authRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userLeavePolicyRepository.findById(userId)).thenReturn(Optional.empty());
        when(leaveYearlyBalanceRepository.findCurrentBalance(any(Long.class), any(LocalDate.class)))
            .thenReturn(null);

        UserContextResponse response = userService.getUserContext(userId);

        assertThat(response.user()).isNotNull();
        assertThat(response.user().onboardingCompleted()).isFalse();
        assertThat(response.leavePolicy()).isNull();
        assertThat(response.leaveBalance()).isNull();
    }

    private User createUser() {
        User user = User.builder()
            .username("oauth-user")
            .email("user@lawding.com")
            .provider("GOOGLE")
            .build();
        user.updateNickname("lawding");
        return user;
    }
}

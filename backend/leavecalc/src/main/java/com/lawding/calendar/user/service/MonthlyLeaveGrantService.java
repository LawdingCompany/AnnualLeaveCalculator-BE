package com.lawding.calendar.user.service;

import com.lawding.calendar.user.entity.LeaveGrant;
import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.entity.UserLeavePolicy;
import com.lawding.calendar.user.enums.LeaveGrantSource;
import com.lawding.calendar.user.enums.LeaveGrantType;
import com.lawding.calendar.user.repository.LeaveGrantRepository;
import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import com.lawding.calendar.user.repository.UserLeavePolicyRepository;
import com.lawding.notification.entity.UserNotification;
import com.lawding.notification.repository.UserNotificationRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonthlyLeaveGrantService {
    private static final int MAX_MONTHLY_GRANTS = 11;
    private final LeaveYearlyBalanceRepository balanceRepository;
    private final LeaveGrantRepository grantRepository;
    private final UserNotificationRepository notificationRepository;
    private final UserLeavePolicyRepository policyRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void grantDueMonthlyLeaves(Long userId, LocalDate today) {
        UserLeavePolicy policy = policyRepository.findById(userId).orElse(null);
        if (policy == null) return;
        LocalDate hireDate = policy.getHireDate();
        LocalDate expiresOn = hireDate.plusYears(1).minusDays(1);
        if (today.isAfter(expiresOn)) return;

        LeaveYearlyBalance balance = balanceRepository.findCurrentBalanceForUpdate(
            policy.getUserId(), today).orElse(null);
        if (balance == null) return;

        int minutes = calculateDailyMinutes(policy);
        if (minutes <= 0) return;

        LocalDate acceptedDate = policy.getCreatedAt() == null
            ? policy.getAcceptedAt().toLocalDate()
            : policy.getCreatedAt().toLocalDate();
        for (int sequence = 1; sequence <= MAX_MONTHLY_GRANTS; sequence++) {
            LocalDate grantedDate = hireDate.plusMonths(sequence);
            if (grantedDate.isAfter(today)) break;
            if (!grantedDate.isAfter(acceptedDate)) continue;

            String sourceKey = "MONTHLY:" + sequence + ":" + grantedDate;
            if (grantRepository.existsByUser_IdAndSourceKey(policy.getUserId(), sourceKey)) continue;

            grantRepository.save(LeaveGrant.create(policy.getUser(), balance, LeaveGrantType.MONTHLY,
                LeaveGrantSource.MONTHLY_BATCH, sourceKey, minutes, 0, grantedDate, grantedDate, expiresOn));
            balance.addGrantedMinutes(minutes);

            if (!notificationRepository.existsByUser_IdAndEventKey(policy.getUserId(), sourceKey)) {
                notificationRepository.save(UserNotification.monthlyGrant(policy.getUser(), minutes, sourceKey));
            }
            log.info("Granted monthly leave. userId={}, sequence={}, minutes={}, expiresOn={}",
                policy.getUserId(), sequence, minutes, expiresOn);
        }
    }

    private int calculateDailyMinutes(UserLeavePolicy policy) {
        if (policy.getWorkPattern() == null) return 0;
        long work = policy.getWorkPattern().totalWeeklyMinutes();
        long breaks = policy.getBreakTimePattern() == null ? 0
            : policy.getBreakTimePattern().totalWeeklyMinutes();
        int days = policy.getWorkPattern().weeklyWorkingDays();
        if (days == 0) return 0;
        return BigDecimal.valueOf(Math.max(0, work - breaks))
            .divide(BigDecimal.valueOf(days), 0, java.math.RoundingMode.HALF_UP).intValue();
    }
}

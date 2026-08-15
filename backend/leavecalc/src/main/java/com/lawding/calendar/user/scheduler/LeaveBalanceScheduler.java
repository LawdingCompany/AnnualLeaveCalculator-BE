package com.lawding.calendar.user.scheduler;

import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import com.lawding.calendar.user.service.LeaveBalanceRolloverService;
import com.lawding.calendar.user.service.MonthlyLeaveGrantService;
import com.lawding.calendar.user.repository.UserLeavePolicyRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveBalanceScheduler {

    private final LeaveYearlyBalanceRepository leaveYearlyBalanceRepository;
    private final LeaveBalanceRolloverService leaveBalanceRolloverService;
    private final UserLeavePolicyRepository userLeavePolicyRepository;
    private final MonthlyLeaveGrantService monthlyLeaveGrantService;

    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Seoul")
    public void finalizeExpiredBalancesAndCreateNext() {
        LocalDate today = LocalDate.now();

        userLeavePolicyRepository.findAllUserIds().forEach(userId -> {
            try {
                monthlyLeaveGrantService.grantDueMonthlyLeaves(userId, today);
            } catch (RuntimeException e) {
                log.error("Failed to grant monthly leave. userId={}", userId, e);
            }
        });

        for (Long balanceId : leaveYearlyBalanceRepository.findExpiredBalanceIds(today)) {
            try {
                leaveBalanceRolloverService.rolloverExpiredBalance(balanceId, today);
            } catch (RuntimeException e) {
                log.error("Failed to rollover leave balance. balanceId={}", balanceId, e);
            }
        }
    }
}

package com.lawding.calendar.user.scheduler;

import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import com.lawding.calendar.user.service.LeaveBalanceRolloverService;
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

    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Seoul")
    public void finalizeExpiredBalancesAndCreateNext() {
        LocalDate today = LocalDate.now();

        for (Long balanceId : leaveYearlyBalanceRepository.findExpiredBalanceIds(today)) {
            try {
                leaveBalanceRolloverService.rolloverExpiredBalance(balanceId, today);
            } catch (RuntimeException e) {
                log.error("Failed to rollover leave balance. balanceId={}", balanceId, e);
            }
        }
    }
}

package com.lawding.calendar.user.scheduler;

import com.lawding.calendar.user.client.AnnualLeaveCalculationClient;
import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.entity.UserLeavePolicy;
import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import com.lawding.calendar.user.repository.UserLeavePolicyRepository;
import com.lawding.global.common.dto.DatePeriod;
import com.lawding.leavecalc.LeavePolicyCalculator;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveBalanceScheduler {

    private final LeaveYearlyBalanceRepository leaveYearlyBalanceRepository;
    private final UserLeavePolicyRepository userLeavePolicyRepository;
    private final LeavePolicyCalculator leavePolicyCalculator;
    private final AnnualLeaveCalculationClient annualLeaveCalculationClient;

    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void finalizeExpiredBalancesAndCreateNext() {
        LocalDate today = LocalDate.now();

        for (LeaveYearlyBalance balance : leaveYearlyBalanceRepository
            .findAllByIsFinalizedFalseAndEndDateBefore(today)) {
            try {
                createNextBalance(balance, today);
            } catch (RuntimeException e) {
                log.error("Failed to rollover leave balance. balanceId={}, userId={}",
                    balance.getId(), balance.getUser().getId(), e);
            }
        }
    }

    private void createNextBalance(LeaveYearlyBalance previousBalance, LocalDate referenceDate) {
        Long userId = previousBalance.getUser().getId();
        UserLeavePolicy policy = userLeavePolicyRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User leave policy not found. userId=" + userId));

        LocalDate nextStartDate = previousBalance.getEndDate().plusDays(1);
        DatePeriod nextPeriod = leavePolicyCalculator.calculateCurrentPeriod(
            policy.getLeaveAccrualBasis(),
            policy.getHireDate(),
            policy.getFiscalYearBaseMonth(),
            nextStartDate
        );

        BigDecimal totalLeaveDays = annualLeaveCalculationClient.calculateTotalLeaveDays(
            policy.getLeaveAccrualBasis(),
            policy.getHireDate(),
            referenceDate
        );

        int totalLeaveMinutes = leavePolicyCalculator.convertLeaveDaysToMinutes(
            totalLeaveDays,
            previousBalance.getAvgDailyWorkHours()
        );

        previousBalance.finalizeBalance();
        leaveYearlyBalanceRepository.save(LeaveYearlyBalance.create(
            previousBalance.getUser(),
            nextPeriod.startDate(),
            nextPeriod.endDate(),
            previousBalance.getWeeklyWorkingDays(),
            previousBalance.getAvgDailyWorkHours(),
            totalLeaveMinutes,
            0
        ));

        log.info("Rolled over leave balance. previousBalanceId={}, userId={}, nextStartDate={}",
            previousBalance.getId(), userId, nextPeriod.startDate());
    }
}

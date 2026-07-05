package com.lawding.calendar.user.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveBalanceRolloverService {

    private final LeaveYearlyBalanceRepository leaveYearlyBalanceRepository;
    private final UserLeavePolicyRepository userLeavePolicyRepository;
    private final LeavePolicyCalculator leavePolicyCalculator;
    private final AnnualLeaveCalculationClient annualLeaveCalculationClient;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rolloverExpiredBalance(Long balanceId, LocalDate today) {
        LeaveYearlyBalance previousBalance = leaveYearlyBalanceRepository
            .findExpiredBalanceForUpdate(balanceId, today)
            .orElse(null);

        if (previousBalance == null) {
            log.info("Skip leave balance rollover. Already finalized or not expired. balanceId={}", balanceId);
            return;
        }

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

        if (leaveYearlyBalanceRepository.existsByUser_IdAndStartDateAndEndDate(
            userId,
            nextPeriod.startDate(),
            nextPeriod.endDate()
        )) {
            previousBalance.finalizeBalance();
            log.warn(
                "Skip creating duplicate leave balance. Finalized previous only. previousBalanceId={}, userId={}, nextStartDate={}, nextEndDate={}",
                previousBalance.getId(), userId, nextPeriod.startDate(), nextPeriod.endDate()
            );
            return;
        }

        BigDecimal totalLeaveDays = annualLeaveCalculationClient.calculateTotalLeaveDays(
            policy.getLeaveAccrualBasis(),
            policy.getHireDate(),
            today
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

        log.info(
            "Rolled over leave balance. previousBalanceId={}, userId={}, totalLeaveDays={}, totalLeaveMinutes={}, nextStartDate={}, nextEndDate={}",
            previousBalance.getId(), userId, totalLeaveDays, totalLeaveMinutes,
            nextPeriod.startDate(), nextPeriod.endDate()
        );
    }
}

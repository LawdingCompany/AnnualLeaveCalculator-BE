package com.lawding.calendar.user.service;

import com.lawding.calendar.user.client.AnnualLeaveCalculationClient;
import com.lawding.calendar.user.client.dto.AnnualLeaveCalculationDetail;
import com.lawding.calendar.user.client.dto.AnnualLeaveCalculationResponse;
import com.lawding.calendar.user.client.dto.LambdaDatePeriod;
import com.lawding.calendar.user.entity.LeaveGrant;
import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.entity.UserLeavePolicy;
import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import com.lawding.calendar.user.repository.LeaveGrantRepository;
import com.lawding.calendar.user.enums.LeaveGrantSource;
import com.lawding.calendar.user.enums.LeaveGrantType;
import com.lawding.calendar.user.repository.UserLeavePolicyRepository;
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
    private final LeaveGrantRepository leaveGrantRepository;

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
        AnnualLeaveCalculationResponse calculation = annualLeaveCalculationClient.calculate(
            policy.getLeaveAccrualBasis(),
            policy.getHireDate(),
            policy.getFiscalYearBaseMonth(),
            nextStartDate
        );
        AnnualLeaveCalculationDetail detail = calculation.calculationDetail();
        BigDecimal totalLeaveDays = detail.totalLeaveDays();

        LambdaDatePeriod availablePeriod = resolveAvailablePeriod(detail);

        if (leaveYearlyBalanceRepository.existsByUser_IdAndStartDateAndEndDate(
            userId, availablePeriod.startDate(), availablePeriod.endDate())) {
            previousBalance.finalizeBalance();
            log.warn("Skip duplicate leave balance. userId={}, startDate={}, endDate={}",
                userId, availablePeriod.startDate(), availablePeriod.endDate());
            return;
        }

        int totalLeaveMinutes = leavePolicyCalculator.convertLeaveDaysToMinutes(
            totalLeaveDays,
            previousBalance.getAvgDailyWorkHours()
        );

        previousBalance.finalizeBalance();
        LeaveYearlyBalance newBalance = leaveYearlyBalanceRepository.save(LeaveYearlyBalance.create(
            previousBalance.getUser(),
            availablePeriod.startDate(),
            availablePeriod.endDate(),
            previousBalance.getWeeklyWorkingDays(),
            previousBalance.getAvgDailyWorkHours(),
            totalLeaveMinutes,
            0
        ));
        createLambdaGrants(previousBalance, newBalance, calculation, nextStartDate);

        log.info(
            "Rolled over leave balance. previousBalanceId={}, userId={}, totalLeaveDays={}, totalLeaveMinutes={}, nextStartDate={}, nextEndDate={}",
            previousBalance.getId(), userId, totalLeaveDays, totalLeaveMinutes,
            availablePeriod.startDate(), availablePeriod.endDate()
        );
    }

    private LambdaDatePeriod resolveAvailablePeriod(AnnualLeaveCalculationDetail detail) {
        if (detail.availablePeriod() != null) return detail.availablePeriod();
        AnnualLeaveCalculationDetail monthly = detail.monthlyDetail();
        AnnualLeaveCalculationDetail prorated = detail.proratedDetail();
        if (monthly == null || prorated == null
            || monthly.availablePeriod() == null || prorated.availablePeriod() == null) {
            throw new IllegalStateException("Lambda response has no available period");
        }
        LocalDate start = monthly.availablePeriod().startDate().isBefore(prorated.availablePeriod().startDate())
            ? monthly.availablePeriod().startDate() : prorated.availablePeriod().startDate();
        LocalDate end = monthly.availablePeriod().endDate().isAfter(prorated.availablePeriod().endDate())
            ? monthly.availablePeriod().endDate() : prorated.availablePeriod().endDate();
        return new LambdaDatePeriod(start, end);
    }

    private void createLambdaGrants(LeaveYearlyBalance previousBalance, LeaveYearlyBalance balance,
        AnnualLeaveCalculationResponse response, LocalDate grantedDate) {
        AnnualLeaveCalculationDetail detail = response.calculationDetail();
        if ("MONTHLY_AND_PRORATED".equals(response.leaveType())) {
            createGrant(previousBalance, balance, LeaveGrantType.MONTHLY, detail.monthlyDetail(), grantedDate,
                response.calculationId() + ":MONTHLY");
            createGrant(previousBalance, balance, LeaveGrantType.PRORATED, detail.proratedDetail(), grantedDate,
                response.calculationId() + ":PRORATED");
            return;
        }
        LeaveGrantType type = switch (response.leaveType()) {
            case "MONTHLY" -> LeaveGrantType.MONTHLY;
            case "PRORATED" -> LeaveGrantType.PRORATED;
            default -> LeaveGrantType.ANNUAL;
        };
        createGrant(previousBalance, balance, type, detail, grantedDate, response.calculationId());
    }

    private void createGrant(LeaveYearlyBalance previousBalance, LeaveYearlyBalance balance,
        LeaveGrantType type, AnnualLeaveCalculationDetail detail, LocalDate grantedDate, String sourceKey) {
        if (detail == null || detail.availablePeriod() == null) {
            throw new IllegalStateException("Lambda detail has no available period");
        }
        int minutes = leavePolicyCalculator.convertLeaveDaysToMinutes(
            detail.totalLeaveDays(), previousBalance.getAvgDailyWorkHours());
        if (type == LeaveGrantType.MONTHLY) {
            minutes = Math.max(0, minutes - leaveGrantRepository.sumActiveGrantedByType(
                previousBalance.getUser().getId(), LeaveGrantType.MONTHLY, grantedDate));
        }
        if (minutes == 0) return;
        leaveGrantRepository.save(LeaveGrant.create(
            previousBalance.getUser(), balance, type, LeaveGrantSource.LAMBDA, sourceKey,
            minutes, 0, grantedDate, detail.availablePeriod().startDate(), detail.availablePeriod().endDate()
        ));
    }
}

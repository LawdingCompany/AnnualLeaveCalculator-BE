package com.lawding.calendar.user.service.impl;

import com.lawding.auth.entity.User;
import com.lawding.auth.repository.AuthRepository;
import com.lawding.calendar.user.dto.request.UserLeavePolicyRequest;
import com.lawding.calendar.user.dto.response.DashboardResponse;
import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.entity.UserLeavePolicy;
import com.lawding.calendar.user.enums.LeaveAccrualBasis;
import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import com.lawding.calendar.user.repository.UserLeavePolicyRepository;
import com.lawding.calendar.user.service.UserService;
import com.lawding.global.common.dto.DatePeriod;
import com.lawding.leavecalc.LeavePolicyCalculator;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final LeavePolicyCalculator LPCalculator;
    private final AuthRepository authRepository;
    private final UserLeavePolicyRepository userLeavePolicyRepository;
    private final LeaveYearlyBalanceRepository leaveYearlyBalanceRepository;


    @Transactional(readOnly = true)
    @Override
    public DashboardResponse getDashBoard(Long userId) {
        User user = findUser(userId);
        LeaveYearlyBalance balance = findCurrentBalance(userId, LocalDate.now());
        int remainingMinutes = balance.getRemainingMinutes();

        return new DashboardResponse(
            user.getNickname(),
            remainingMinutes,
            calculateRemainingDays(remainingMinutes, balance.getAvgDailyWorkHours()),
            calculateRemainingHours(remainingMinutes)
        );
    }

    @Transactional
    @Override
    public void saveUserLeavePolicy(Long userId, UserLeavePolicyRequest request) {
        if (request.leaveAccrualBasis() == LeaveAccrualBasis.FISCAL_YEAR
            && request.fiscalYearBaseMonth() == null) {
            throw new IllegalArgumentException("회계연도 기준일에는 회계연도 시작(월)일이 필요합니다.");
        }

        User user = findUser(userId);

        LocalDateTime acceptedAt = LocalDateTime.now();

        // 1. 기존의 유저 테이블에서 유저를 찾아서 닉네임을 업데이트해야함.
        user.updateNickname(request.nickname());

        // 2. Request 정보 기반으로 UserLeavePolicy 채워서 저장하기
        LocalDate nextLeaveAccrualDate = LPCalculator.calculateNextLeaveAccrualDate(
            request.leaveAccrualBasis(),
            request.joinDate(),
            request.fiscalYearBaseMonth());

        UserLeavePolicy policy = UserLeavePolicy.create(
            user,
            acceptedAt,
            request.leaveAccrualBasis(),
            request.joinDate(),
            request.fiscalYearBaseMonth(),
            request.companySize(),
            request.workPattern(),
            nextLeaveAccrualDate
        );

        userLeavePolicyRepository.save(policy);

        // 3. 연차 계산을 통해서 금년도 LeaveYearlyBalence를 채워넣어야함.
        DatePeriod period = LPCalculator.calculateCurrentPeriod(
            request.leaveAccrualBasis(),
            request.joinDate(),
            request.fiscalYearBaseMonth(),
            LocalDate.now()
        );

        int weeklyWorkingDays =
            LPCalculator.calculateWeeklyWorkingDays(request.workPattern());

        BigDecimal avgDailyWorkHours =
            LPCalculator.calculateAvgDailyWorkHours(request.workPattern());

        int totalLeaveMinutes =
            LPCalculator.convertLeaveDaysToMinutes(
                BigDecimal.valueOf(request.totalLeave()),
                avgDailyWorkHours);

        int usedLeaveMinutes =
            LPCalculator.convertLeaveDaysToMinutes(
                BigDecimal.valueOf(request.usedLeave()),
                avgDailyWorkHours);

        LeaveYearlyBalance balance = LeaveYearlyBalance.create(
            user,
            period.startDate(),
            period.endDate(),
            weeklyWorkingDays,
            avgDailyWorkHours,
            totalLeaveMinutes,
            usedLeaveMinutes);

        leaveYearlyBalanceRepository.save(balance);
        user.completeOnboarding();
    }

    private User findUser(Long userId) {
        validateUserId(userId);
        return authRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private LeaveYearlyBalance findCurrentBalance(Long userId, LocalDate targetDate) {
        LeaveYearlyBalance balance = leaveYearlyBalanceRepository.findCurrentBalance(userId, targetDate);
        if (balance == null) {
            throw new EntityNotFoundException("Current leave balance not found with userId: " + userId);
        }
        return balance;
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Authenticated user id is required.");
        }
    }

    private BigDecimal calculateRemainingDays(int remainingMinutes, BigDecimal avgDailyWorkHours) {
        if (avgDailyWorkHours == null || avgDailyWorkHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(3);
        }

        BigDecimal dailyWorkMinutes = avgDailyWorkHours.multiply(BigDecimal.valueOf(60));
        return BigDecimal.valueOf(remainingMinutes)
            .divide(dailyWorkMinutes, 3, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRemainingHours(int remainingMinutes) {
        return BigDecimal.valueOf(remainingMinutes)
            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}

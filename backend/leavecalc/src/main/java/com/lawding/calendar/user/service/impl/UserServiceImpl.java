package com.lawding.calendar.user.service.impl;

import com.lawding.auth.entity.User;
import com.lawding.auth.repository.AuthRepository;
import com.lawding.calendar.calendarevent.entity.CalendarEvent;
import com.lawding.calendar.calendarevent.repository.CalendarEventRepository;
import com.lawding.calendar.user.dto.request.UserLeavePolicyRequest;
import com.lawding.calendar.user.dto.request.UserRequest;
import com.lawding.calendar.user.dto.response.DashboardResponse;
import com.lawding.calendar.user.dto.response.LeaveDashboardResponse;
import com.lawding.calendar.user.dto.response.LeaveYearlyBalanceResponse;
import com.lawding.calendar.user.dto.response.RecentLeaveUsageResponse;
import com.lawding.calendar.user.dto.response.UserLeavePolicyResponse;
import com.lawding.calendar.user.dto.response.UserResponse;
import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.entity.UserLeavePolicy;
import com.lawding.calendar.user.enums.LeaveAccrualBasis;
import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import com.lawding.calendar.user.repository.UserLeavePolicyRepository;
import com.lawding.calendar.user.service.UserService;
import com.lawding.global.common.dto.DatePeriod;
import com.lawding.global.exception.ClientException;
import com.lawding.global.exception.ErrorCode;
import com.lawding.leavecalc.LeavePolicyCalculator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
    private final CalendarEventRepository calendarEventRepository;

    @Transactional(readOnly = true)
    @Override
    public DashboardResponse getDashBoard(Long userId) {
        User user = findActiveUser(userId);
        LeaveYearlyBalance balance = findCurrentBalance(userId, LocalDate.now());
        int remainingMinutes = balance.getRemainingMinutes();

        return new DashboardResponse(
            user.getNickname(),
            remainingMinutes,
            calculateRemainingDays(remainingMinutes, balance.getAvgDailyWorkHours()),
            calculateRemainingHours(remainingMinutes)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUser(Long userId) {
        return UserResponse.from(findActiveUser(userId));
    }

    @Transactional
    @Override
    public UserResponse updateUser(Long userId, UserRequest request) {
        User user = findActiveUser(userId);
        user.updateProfile(request.username(), request.email(), request.provider(), request.nickname());
        return UserResponse.from(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = findActiveUser(userId);
        user.softDelete(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    @Override
    public UserLeavePolicyResponse getUserLeavePolicy(Long userId) {
        findActiveUser(userId);
        return UserLeavePolicyResponse.from(findPolicy(userId));
    }

    @Transactional
    @Override
    public void saveUserLeavePolicy(Long userId, UserLeavePolicyRequest request) {
        upsertUserLeavePolicy(userId, request);
    }

    @Transactional
    @Override
    public UserLeavePolicyResponse updateUserLeavePolicy(Long userId, UserLeavePolicyRequest request) {
        return UserLeavePolicyResponse.from(upsertUserLeavePolicy(userId, request));
    }

    @Transactional
    @Override
    public void deleteUserLeavePolicy(Long userId) {
        findActiveUser(userId);
        calendarEventRepository.deleteByUser_Id(userId);
        leaveYearlyBalanceRepository.deleteByUser_Id(userId);
        userLeavePolicyRepository.deleteByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public LeaveYearlyBalanceResponse getLatestLeaveYearlyBalance(Long userId) {
        findActiveUser(userId);
        LeaveYearlyBalance balance = leaveYearlyBalanceRepository
            .findTopByUser_IdOrderByIdDesc(userId)
            .orElseThrow(() -> new ClientException(ErrorCode.LEAVE_BALANCE_NOT_FOUND));
        return LeaveYearlyBalanceResponse.from(balance);
    }

    @Transactional(readOnly = true)
    @Override
    public LeaveDashboardResponse getLeaveDashboard(Long userId) {
        findActiveUser(userId);
        LeaveYearlyBalance balance = leaveYearlyBalanceRepository
            .findTopByUser_IdOrderByIdDesc(userId)
            .orElseThrow(() -> new ClientException(ErrorCode.LEAVE_BALANCE_NOT_FOUND));

        LocalDateTime periodStart = balance.getStartDate().atStartOfDay();
        LocalDateTime periodEnd = balance.getEndDate().atTime(LocalTime.MAX);
        List<CalendarEvent> leaveEvents = calendarEventRepository
            .findAllByUser_IdAndIsLeaveEventTrueAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualOrderByStartDatetimeDesc(
                userId,
                periodEnd,
                periodStart
            );

        return new LeaveDashboardResponse(
            balance.getRemainingMinutes(),
            balance.getAvgDailyWorkHours(),
            balance.getTotalLeaveMinutes(),
            balance.getEndDate().plusDays(1),
            balance.getRemainingMinutes(),
            balance.getStartDate(),
            balance.getEndDate(),
            leaveEvents.stream()
                .map(RecentLeaveUsageResponse::from)
                .toList()
        );
    }

    @Transactional
    public void hardDeleteUsersDue(LocalDateTime now) {
        for (User user : authRepository.findAllByDeletedTrueAndHardDeleteScheduledAtLessThanEqual(now)) {
            Long userId = user.getId();
            calendarEventRepository.deleteByUser_Id(userId);
            leaveYearlyBalanceRepository.deleteByUser_Id(userId);
            userLeavePolicyRepository.deleteByUser_Id(userId);
            authRepository.delete(user);
            log.info("Hard deleted userId={}", userId);
        }
    }

    private UserLeavePolicy upsertUserLeavePolicy(Long userId, UserLeavePolicyRequest request) {
        LeaveAccrualBasis basis = LeaveAccrualBasis.fromCode(request.leaveAccrualBasis());
        validateLeavePolicyRequest(request, basis);

        User user = findActiveUser(userId);
        LocalDateTime acceptedAt = LocalDateTime.now();
        user.updateNickname(request.nickname());

        UserLeavePolicy policy = userLeavePolicyRepository.findById(userId)
            .map(existing -> {
                existing.update(
                    acceptedAt,
                    basis,
                    request.hireDate(),
                    request.fiscalYearBaseMonth(),
                    request.companySize(),
                    request.workPattern(),
                    request.breakTimePattern()
                );
                return existing;
            })
            .orElseGet(() -> UserLeavePolicy.create(
                user,
                acceptedAt,
                basis,
                request.hireDate(),
                request.fiscalYearBaseMonth(),
                request.companySize(),
                request.workPattern(),
                request.breakTimePattern()
            ));

        UserLeavePolicy savedPolicy = userLeavePolicyRepository.save(policy);
        replaceCurrentBalance(user, request, basis);
        user.completeOnboarding();
        return savedPolicy;
    }

    private void replaceCurrentBalance(User user, UserLeavePolicyRequest request, LeaveAccrualBasis basis) {
        DatePeriod period = LPCalculator.calculateCurrentPeriod(
            basis,
            request.hireDate(),
            request.fiscalYearBaseMonth(),
            LocalDate.now()
        );

        BigDecimal avgDailyWorkHours =
            LPCalculator.calculateAvgDailyWorkHours(request.workPattern(), request.breakTimePattern());

        int weeklyWorkingDays = LPCalculator.calculateWeeklyWorkingDays(request.workPattern());
        int totalLeaveMinutes = LPCalculator.convertLeaveDaysToMinutes(
            BigDecimal.valueOf(defaultZero(request.totalLeave())),
            avgDailyWorkHours);
        int usedLeaveMinutes = LPCalculator.convertLeaveDaysToMinutes(
            BigDecimal.valueOf(defaultZero(request.usedLeave())),
            avgDailyWorkHours);

        LeaveYearlyBalance currentBalance = leaveYearlyBalanceRepository
            .findCurrentBalance(user.getId(), LocalDate.now());

        if (currentBalance == null) {
            leaveYearlyBalanceRepository.save(LeaveYearlyBalance.create(
                user,
                period.startDate(),
                period.endDate(),
                weeklyWorkingDays,
                avgDailyWorkHours,
                totalLeaveMinutes,
                usedLeaveMinutes
            ));
            return;
        }

        currentBalance.updateBalance(
            period.startDate(),
            period.endDate(),
            weeklyWorkingDays,
            avgDailyWorkHours,
            totalLeaveMinutes,
            usedLeaveMinutes
        );
    }

    private void validateLeavePolicyRequest(UserLeavePolicyRequest request, LeaveAccrualBasis basis) {
        if (basis == LeaveAccrualBasis.FISCAL_YEAR && request.fiscalYearBaseMonth() == null) {
            throw new ClientException(ErrorCode.LEAVE_POLICY_INVALID, "회계연도 기준에서는 fiscalYearBaseMonth가 필요합니다.");
        }
        if (request.hireDate() == null) {
            throw new ClientException(ErrorCode.LEAVE_POLICY_INVALID, "hireDate는 필수입니다.");
        }
        if (request.workPattern() == null) {
            throw new ClientException(ErrorCode.LEAVE_POLICY_INVALID, "workPattern은 필수입니다.");
        }
    }

    private User findActiveUser(Long userId) {
        validateUserId(userId);
        return authRepository.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));
    }

    private UserLeavePolicy findPolicy(Long userId) {
        return userLeavePolicyRepository.findById(userId)
            .orElseThrow(() -> new ClientException(ErrorCode.LEAVE_POLICY_NOT_FOUND));
    }

    private LeaveYearlyBalance findCurrentBalance(Long userId, LocalDate targetDate) {
        LeaveYearlyBalance balance = leaveYearlyBalanceRepository.findCurrentBalance(userId, targetDate);
        if (balance == null) {
            throw new ClientException(ErrorCode.CURRENT_LEAVE_BALANCE_NOT_FOUND);
        }
        return balance;
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new ClientException(ErrorCode.UNAUTHORIZED);
        }
    }

    private int defaultZero(Integer value) {
        return value == null ? 0 : value;
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

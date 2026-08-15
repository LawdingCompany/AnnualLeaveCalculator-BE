package com.lawding.calendar.user.service.impl;

import com.lawding.auth.entity.User;
import com.lawding.auth.repository.AuthRepository;
import com.lawding.calendar.calendarevent.entity.CalendarEvent;
import com.lawding.calendar.calendarevent.repository.CalendarEventRepository;
import com.lawding.calendar.calendarevent.repository.CalendarEventLeaveAllocationRepository;
import com.lawding.calendar.user.dto.request.UserLeavePolicyRequest;
import com.lawding.calendar.user.dto.request.UserNicknameRequest;
import com.lawding.calendar.user.dto.response.DashboardResponse;
import com.lawding.calendar.user.dto.response.LeaveDashboardResponse;
import com.lawding.calendar.user.dto.response.LeaveYearlyBalanceResponse;
import com.lawding.calendar.user.dto.response.RecentLeaveUsageResponse;
import com.lawding.calendar.user.dto.response.UserContextResponse;
import com.lawding.calendar.user.dto.response.UserLeavePolicyResponse;
import com.lawding.calendar.user.dto.response.UserResponse;
import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.entity.LeaveGrant;
import com.lawding.calendar.user.entity.UserLeavePolicy;
import com.lawding.calendar.user.enums.LeaveAccrualBasis;
import com.lawding.calendar.user.enums.LeaveGrantSource;
import com.lawding.calendar.user.enums.LeaveGrantType;
import com.lawding.calendar.user.repository.LeaveGrantRepository;
import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import com.lawding.calendar.user.repository.UserLeavePolicyRepository;
import com.lawding.calendar.user.service.UserService;
import com.lawding.calendar.user.service.LeaveLedgerService;
import com.lawding.global.common.dto.DatePeriod;
import com.lawding.global.exception.ClientException;
import com.lawding.global.exception.ErrorCode;
import com.lawding.leavecalc.LeavePolicyCalculator;
import com.lawding.notification.repository.UserNotificationRepository;
import java.math.BigDecimal;
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
    private final CalendarEventLeaveAllocationRepository allocationRepository;
    private final LeaveGrantRepository leaveGrantRepository;
    private final LeaveLedgerService leaveLedgerService;
    private final UserNotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    @Override
    public UserContextResponse getUserContext(Long userId) {
        User user = findActiveUser(userId);
        UserLeavePolicyResponse leavePolicy = userLeavePolicyRepository.findById(userId)
            .map(UserLeavePolicyResponse::from)
            .orElse(null);

        LeaveYearlyBalance currentBalance = leaveYearlyBalanceRepository
            .findCurrentBalance(userId, LocalDate.now());
        LocalDate today = LocalDate.now();
        LeaveYearlyBalanceResponse leaveBalance = currentBalance == null ? null
            : balanceResponse(currentBalance, userId, today);

        return new UserContextResponse(
            UserResponse.from(user),
            leavePolicy,
            leaveBalance
        );
    }

    @Transactional(readOnly = true)
    @Override
    public DashboardResponse getDashBoard(Long userId) {
        User user = findActiveUser(userId);
        LeaveYearlyBalance balance = findCurrentBalance(userId, LocalDate.now());

        return new DashboardResponse(
            user.getNickname(),
            leaveLedgerService.getRemaining(userId, LocalDate.now()),
            balance.getAvgDailyWorkHours()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUser(Long userId) {
        return UserResponse.from(findActiveUser(userId));
    }

    @Transactional
    @Override
    public UserResponse updateUser(Long userId, UserNicknameRequest request) {
        User user = findActiveUser(userId);
        user.updateNickname(request.nickname());
        return UserResponse.from(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = findActiveUser(userId);
        notificationRepository.deleteByUser_Id(userId);
        allocationRepository.deleteByEvent_User_Id(userId);
        calendarEventRepository.deleteByUser_Id(userId);
        leaveGrantRepository.deleteByUser_Id(userId);
        leaveYearlyBalanceRepository.deleteByUser_Id(userId);
        userLeavePolicyRepository.deleteByUser_Id(userId);
        authRepository.delete(user);
        log.info("Hard deleted userId={}", userId);
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
        notificationRepository.deleteByUser_Id(userId);
        allocationRepository.deleteByEvent_User_Id(userId);
        calendarEventRepository.deleteByUser_Id(userId);
        leaveGrantRepository.deleteByUser_Id(userId);
        leaveYearlyBalanceRepository.deleteByUser_Id(userId);
        userLeavePolicyRepository.deleteByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public LeaveYearlyBalanceResponse getLatestLeaveYearlyBalance(Long userId) {
        findActiveUser(userId);
        LocalDate today = LocalDate.now();
        return balanceResponse(findCurrentBalance(userId, today), userId, today);
    }

    @Transactional
    @Override
    public LeaveYearlyBalanceResponse updateRemainingLeaveMinutes(
        Long userId,
        Integer remainingLeaveMinutes
    ) {
        findActiveUser(userId);
        LeaveYearlyBalance balance = leaveYearlyBalanceRepository
            .findCurrentBalanceForUpdate(userId, LocalDate.now())
            .orElseThrow(() -> new ClientException(ErrorCode.CURRENT_LEAVE_BALANCE_NOT_FOUND));

        leaveLedgerService.adjustRemaining(balance, remainingLeaveMinutes);

        return balanceResponse(balance, userId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    @Override
    public LeaveDashboardResponse getLeaveDashboard(Long userId) {
        findActiveUser(userId);
        UserLeavePolicy policy = findPolicy(userId);
        LeaveYearlyBalance balance = findCurrentBalance(userId, LocalDate.now());
        int remaining = leaveLedgerService.getRemaining(userId, LocalDate.now());
        int total = leaveLedgerService.getTotal(userId, LocalDate.now());

        LocalDateTime periodStart = balance.getStartDate().atStartOfDay();
        LocalDateTime periodEnd = balance.getEndDate().atTime(LocalTime.MAX);
        List<CalendarEvent> leaveEvents = calendarEventRepository
            .findAllByUser_IdAndIsLeaveEventTrueAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualOrderByStartDatetimeDesc(
                userId,
                periodEnd,
                periodStart
            );

        return new LeaveDashboardResponse(
            remaining,
            balance.getAvgDailyWorkHours(),
            total,
            policy.getLeaveAccrualBasis().getCode(),
            policy.getLeaveAccrualBasis() == LeaveAccrualBasis.FISCAL_YEAR
                ? policy.getFiscalYearBaseMonth()
                : null,
            balance.getEndDate().plusDays(1),
            remaining,
            balance.getStartDate(),
            balance.getEndDate(),
            leaveEvents.stream()
                .map(RecentLeaveUsageResponse::from)
                .toList()
        );
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
            LeaveYearlyBalance savedBalance = leaveYearlyBalanceRepository.save(LeaveYearlyBalance.create(
                user,
                period.startDate(),
                period.endDate(),
                weeklyWorkingDays,
                avgDailyWorkHours,
                totalLeaveMinutes,
                usedLeaveMinutes
            ));
            leaveGrantRepository.save(LeaveGrant.create(
                user, savedBalance, LeaveGrantType.INITIAL, LeaveGrantSource.ONBOARDING,
                "ONBOARDING:" + savedBalance.getStartDate(), totalLeaveMinutes, usedLeaveMinutes,
                LocalDate.now(), savedBalance.getStartDate(), savedBalance.getEndDate()
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
        if (leaveGrantRepository.findByBalanceIdForUpdate(currentBalance.getId()).isEmpty()) {
            leaveGrantRepository.save(LeaveGrant.create(
                user, currentBalance, LeaveGrantType.INITIAL, LeaveGrantSource.ONBOARDING,
                "ONBOARDING:" + currentBalance.getStartDate(), totalLeaveMinutes, usedLeaveMinutes,
                LocalDate.now(), currentBalance.getStartDate(), currentBalance.getEndDate()
            ));
        } else {
            leaveLedgerService.adjustRemaining(
                currentBalance, Math.max(0, totalLeaveMinutes - usedLeaveMinutes));
        }
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
        return authRepository.findById(userId)
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

    private LeaveYearlyBalanceResponse balanceResponse(
        LeaveYearlyBalance balance, Long userId, LocalDate date) {
        return LeaveYearlyBalanceResponse.from(
            balance,
            leaveLedgerService.getTotal(userId, date),
            leaveLedgerService.getUsed(userId, date),
            leaveLedgerService.getRemaining(userId, date)
        );
    }

}

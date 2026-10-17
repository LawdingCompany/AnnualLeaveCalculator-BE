package com.lawding.calendar.calendarevent.service.impl;

import com.lawding.auth.entity.User;
import com.lawding.auth.repository.AuthRepository;
import com.lawding.calendar.calendarevent.dto.request.CalendarEventRequest;
import com.lawding.calendar.calendarevent.entity.CalendarEvent;
import com.lawding.calendar.calendarevent.repository.CalendarEventRepository;
import com.lawding.calendar.calendarevent.service.CalendarEventService;
import com.lawding.calendar.user.enums.LeaveUsageType;
import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import com.lawding.calendar.user.repository.UserLeavePolicyRepository;
import com.lawding.calendar.user.service.LeaveLedgerService;
import com.lawding.global.exception.ClientException;
import com.lawding.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CalendarEventServiceImpl implements CalendarEventService {

    private final AuthRepository authRepository;
    private final LeaveYearlyBalanceRepository leaveYearlyBalanceRepository;
    private final UserLeavePolicyRepository userLeavePolicyRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final LeaveLedgerService leaveLedgerService;

    @Override
    public CalendarEvent createEvent(Long userId, CalendarEventRequest request) {
        validateEventPeriod(request.startDatetime(), request.endDatetime());
        User user = findUser(userId);
        LeaveYearlyBalance balance = findCurrentBalance(userId, request.startDatetime().toLocalDate());
        int usedLeaveMinutes = effectiveUsedLeaveMinutes(request);
        int allocatedMinutes = validateAndGetAllocatedMinutes(
            userId, balance, request.startDatetime().toLocalDate(), usedLeaveMinutes,
            Boolean.TRUE.equals(request.isLeaveEvent())
        );

        CalendarEvent event = CalendarEvent.create(
            user,
            balance,
            request.title(),
            request.description(),
            request.startDatetime(),
            request.endDatetime(),
            usedLeaveMinutes,
            request.isAllDay(),
            request.isLeaveEvent()
        );

        CalendarEvent savedEvent = calendarEventRepository.save(event);
        if (Boolean.TRUE.equals(request.isLeaveEvent())) {
            leaveLedgerService.allocate(savedEvent, balance, request.startDatetime().toLocalDate(), allocatedMinutes);
        }
        return savedEvent;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CalendarEvent> findEventsByMonth(Long userId, int year, int month) {
        validateUserId(userId);
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEndExclusive = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        return calendarEventRepository
            .findAllByUser_IdAndStartDatetimeLessThanAndEndDatetimeGreaterThanEqualOrderByStartDatetimeAsc(
                userId,
                monthEndExclusive,
                monthStart
            );
    }

    @Transactional(readOnly = true)
    @Override
    public CalendarEvent findEvent(Long userId, Long eventId) {
        validateUserId(userId);
        return findOwnedEvent(userId, eventId);
    }

    @Override
    public void updateEvent(Long userId, Long eventId, CalendarEventRequest request) {
        validateEventPeriod(request.startDatetime(), request.endDatetime());
        CalendarEvent event = findOwnedEvent(userId, eventId);
        LeaveYearlyBalance oldBalance = findBalanceForUpdate(
            event.getLeaveYearlyBalance().getId()
        );
        int newUsedMinutes = effectiveUsedLeaveMinutes(request);
        boolean wasLeaveEvent = Boolean.TRUE.equals(event.getIsLeaveEvent());
        boolean willBeLeaveEvent = Boolean.TRUE.equals(request.isLeaveEvent());

        if (wasLeaveEvent) {
            leaveLedgerService.cancel(event, oldBalance);
        }

        LeaveYearlyBalance newBalance = findCurrentBalance(userId, request.startDatetime().toLocalDate());
        int allocatedMinutes = validateAndGetAllocatedMinutes(
            userId, newBalance, request.startDatetime().toLocalDate(), newUsedMinutes, willBeLeaveEvent
        );

        if (willBeLeaveEvent) {
            leaveLedgerService.allocate(event, newBalance, request.startDatetime().toLocalDate(), allocatedMinutes);
        }

        event.update(
            newBalance,
            request.title(),
            request.description(),
            request.startDatetime(),
            request.endDatetime(),
            newUsedMinutes,
            request.isAllDay(),
            request.isLeaveEvent()
        );
    }

    @Override
    public void deleteEvent(Long userId, Long eventId) {
        CalendarEvent event = findOwnedEvent(userId, eventId);

        if (Boolean.TRUE.equals(event.getIsLeaveEvent())) {
            leaveLedgerService.cancel(event, findBalanceForUpdate(event.getLeaveYearlyBalance().getId()));
        }

        calendarEventRepository.delete(event);
    }

    private User findUser(Long userId) {
        validateUserId(userId);
        return authRepository.findById(userId)
            .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));
    }

    private CalendarEvent findOwnedEvent(Long userId, Long eventId) {
        return calendarEventRepository.findByIdAndUser_Id(eventId, userId)
            .orElseThrow(() -> new ClientException(ErrorCode.CALENDAR_EVENT_NOT_FOUND));
    }

    private LeaveYearlyBalance findCurrentBalance(Long userId, LocalDate targetDate) {
        return leaveYearlyBalanceRepository.findCurrentBalanceForUpdate(userId, targetDate)
            .orElseThrow(() -> new ClientException(ErrorCode.CURRENT_LEAVE_BALANCE_NOT_FOUND));
    }

    private LeaveYearlyBalance findBalanceForUpdate(Long balanceId) {
        return leaveYearlyBalanceRepository.findByIdForUpdate(balanceId)
            .orElseThrow(() -> new ClientException(ErrorCode.LEAVE_BALANCE_NOT_FOUND));
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new ClientException(ErrorCode.UNAUTHORIZED);
        }
    }

    private int normalizeUsedLeaveMinutes(Integer usedLeaveMinutes) {
        return usedLeaveMinutes == null ? 0 : usedLeaveMinutes;
    }

    private int effectiveUsedLeaveMinutes(CalendarEventRequest request) {
        return Boolean.TRUE.equals(request.isLeaveEvent())
            ? normalizeUsedLeaveMinutes(request.usedLeaveMinutes())
            : 0;
    }

    private int validateAndGetAllocatedMinutes(
        Long userId,
        LeaveYearlyBalance balance,
        LocalDate useDate,
        int requestedMinutes,
        boolean isLeaveEvent
    ) {
        if (!isLeaveEvent) {
            return 0;
        }

        LeaveUsageType usageType = userLeavePolicyRepository.findById(userId)
            .map(policy -> policy.getLeaveUsageType())
            .orElse(null);
        if (requestedMinutes <= 0) {
            throw new ClientException(ErrorCode.LEAVE_MINUTES_INVALID, "연차 신청 시간은 0분보다 커야 합니다.");
        }

        int remainingMinutes = leaveLedgerService.getRemaining(userId, useDate);
        if (remainingMinutes <= 0) {
            throw new ClientException(ErrorCode.LEAVE_BALANCE_NOT_ENOUGH);
        }

        if (usageType == null || usageType == LeaveUsageType.HOURLY) {
            if (requestedMinutes > remainingMinutes) {
                throw new ClientException(ErrorCode.LEAVE_BALANCE_NOT_ENOUGH);
            }
            return requestedMinutes;
        }

        int unitMinutes = getDayUnitMinutes(balance, usageType);
        if (unitMinutes <= 0) {
            throw new ClientException(ErrorCode.LEAVE_POLICY_INVALID, "사용 단위는 0분보다 커야 합니다.");
        }
        if (requestedMinutes % unitMinutes != 0) {
            throw new ClientException(ErrorCode.LEAVE_MINUTES_INVALID,
                "신청 시간은 사용자 연차 유형의 사용 단위에 맞아야 합니다.");
        }

        int fullUnits = remainingMinutes / unitMinutes;
        int maxUnits = fullUnits + (remainingMinutes % unitMinutes == 0 ? 0 : 1);
        if (requestedMinutes / unitMinutes > maxUnits) {
            throw new ClientException(ErrorCode.LEAVE_BALANCE_NOT_ENOUGH);
        }
        return Math.min(requestedMinutes, remainingMinutes);
    }

    private int getDayUnitMinutes(LeaveYearlyBalance balance, LeaveUsageType usageType) {
        BigDecimal fraction = switch (usageType) {
            case QUARTER_DAY -> new BigDecimal("0.25");
            case HALF_DAY -> new BigDecimal("0.5");
            case FULL_DAY -> BigDecimal.ONE;
            case HOURLY -> throw new IllegalArgumentException("시간 유형은 일 단위 환산을 하지 않습니다.");
        };
        try {
            return balance.getAvgDailyWorkHours()
                .multiply(BigDecimal.valueOf(60))
                .multiply(fraction)
                .setScale(0, RoundingMode.UNNECESSARY)
                .intValueExact();
        } catch (ArithmeticException | NullPointerException exception) {
            throw new ClientException(ErrorCode.LEAVE_POLICY_INVALID,
                "평균 근로시간으로 사용 단위를 분 단위로 정확히 계산할 수 없습니다.");
        }
    }

    private void validateEventPeriod(LocalDateTime startDatetime, LocalDateTime endDatetime) {
        if (startDatetime.isAfter(endDatetime)) {
            throw new ClientException(ErrorCode.CALENDAR_EVENT_PERIOD_INVALID, "일정 시작 시간은 종료 시간보다 이후일 수 없습니다.");
        }
    }
}

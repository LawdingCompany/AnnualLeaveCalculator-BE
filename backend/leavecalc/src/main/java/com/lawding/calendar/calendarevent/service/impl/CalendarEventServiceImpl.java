package com.lawding.calendar.calendarevent.service.impl;

import com.lawding.auth.entity.User;
import com.lawding.auth.repository.AuthRepository;
import com.lawding.calendar.calendarevent.dto.request.CalendarEventRequest;
import com.lawding.calendar.calendarevent.entity.CalendarEvent;
import com.lawding.calendar.calendarevent.repository.CalendarEventRepository;
import com.lawding.calendar.calendarevent.service.CalendarEventService;
import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final CalendarEventRepository calendarEventRepository;

    @Override
    public void createEvent(Long userId, CalendarEventRequest request) {
        validateEventPeriod(request.startDatetime(), request.endDatetime());
        User user = findUser(userId);
        LeaveYearlyBalance balance = findCurrentBalance(userId, request.startDatetime().toLocalDate());
        int usedLeaveMinutes = effectiveUsedLeaveMinutes(request);

        if (Boolean.TRUE.equals(request.isLeaveEvent())) {
            balance.useLeave(usedLeaveMinutes);
        }

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

        calendarEventRepository.save(event);
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
        LeaveYearlyBalance oldBalance = event.getLeaveYearlyBalance();
        int oldUsedMinutes = normalizeUsedLeaveMinutes(event.getUsedLeaveMinutes());
        int newUsedMinutes = effectiveUsedLeaveMinutes(request);
        boolean wasLeaveEvent = Boolean.TRUE.equals(event.getIsLeaveEvent());
        boolean willBeLeaveEvent = Boolean.TRUE.equals(request.isLeaveEvent());

        if (wasLeaveEvent) {
            oldBalance.cancelUsedLeave(oldUsedMinutes);
        }

        LeaveYearlyBalance newBalance = findCurrentBalance(userId, request.startDatetime().toLocalDate());

        if (willBeLeaveEvent) {
            newBalance.useLeave(newUsedMinutes);
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
            event.getLeaveYearlyBalance()
                .cancelUsedLeave(normalizeUsedLeaveMinutes(event.getUsedLeaveMinutes()));
        }

        calendarEventRepository.delete(event);
    }

    private User findUser(Long userId) {
        validateUserId(userId);
        return authRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. id=" + userId));
    }

    private CalendarEvent findOwnedEvent(Long userId, Long eventId) {
        return calendarEventRepository.findByIdAndUser_Id(eventId, userId)
            .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + eventId));
    }

    private LeaveYearlyBalance findCurrentBalance(Long userId, LocalDate targetDate) {
        LeaveYearlyBalance balance = leaveYearlyBalanceRepository.findCurrentBalance(userId, targetDate);
        if (balance == null) {
            throw new EntityNotFoundException("해당 날짜에 사용 가능한 연차 정보가 없습니다.");
        }
        return balance;
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("인증된 사용자 정보가 없습니다.");
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

    private void validateEventPeriod(LocalDateTime startDatetime, LocalDateTime endDatetime) {
        if (startDatetime.isAfter(endDatetime)) {
            throw new IllegalArgumentException("일정 시작 시간은 종료 시간보다 이후일 수 없습니다.");
        }
    }
}

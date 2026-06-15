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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CalendarEventServiceImpl implements CalendarEventService {

    private final AuthRepository authRepository;
    private final LeaveYearlyBalanceRepository leaveYearlyBalanceRepository;
    private final CalendarEventRepository calendarEventRepository;

    @Transactional
    @Override
    public void createEvent(Long userId, CalendarEventRequest request) {

        User user = authRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        LocalDate targetDate = request.startDatetime().toLocalDate();

        LeaveYearlyBalance balance = leaveYearlyBalanceRepository.findCurrentBalance(userId,
            targetDate);

        if (balance == null) {
            throw new EntityNotFoundException("해당 날짜에 사용 가능한 연차 정보가 없습니다.");
        }

        // 연차 이벤트인 경우 차감
        if (Boolean.TRUE.equals(request.isLeaveEvent())) {
            balance.useLeave(request.usedLeaveMinutes());
        }

        CalendarEvent event = CalendarEvent.create(
            user,
            balance,
            request.title(),
            request.description(),
            request.startDatetime(),
            request.endDatetime(),
            request.usedLeaveMinutes(),
            request.isAllDay(),
            request.isLeaveEvent()
        );

        calendarEventRepository.save(event);
    }
}

package com.lawding.calendar.user.service;

import com.lawding.calendar.calendarevent.entity.CalendarEvent;
import com.lawding.calendar.calendarevent.entity.CalendarEventLeaveAllocation;
import com.lawding.calendar.calendarevent.repository.CalendarEventLeaveAllocationRepository;
import com.lawding.calendar.user.entity.LeaveGrant;
import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.repository.LeaveGrantRepository;
import com.lawding.global.exception.ClientException;
import com.lawding.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveLedgerService {
    private final LeaveGrantRepository grantRepository;
    private final CalendarEventLeaveAllocationRepository allocationRepository;

    @Transactional
    public void allocate(CalendarEvent event, LeaveYearlyBalance balance, LocalDate useDate, int minutes) {
        if (minutes < 0) {
            throw new ClientException(ErrorCode.LEAVE_BALANCE_NOT_ENOUGH);
        }
        List<LeaveGrant> grants = grantRepository.findUsableForUpdate(event.getUser().getId(), useDate);
        int pending = minutes;
        for (LeaveGrant grant : grants) {
            if (pending == 0) break;
            int allocated = Math.min(pending, grant.getRemainingMinutes());
            grant.use(allocated);
            allocationRepository.save(CalendarEventLeaveAllocation.create(event, grant, allocated));
            pending -= allocated;
        }
        if (pending > 0) throw new ClientException(ErrorCode.LEAVE_BALANCE_NOT_ENOUGH);
    }

    @Transactional
    public void cancel(CalendarEvent event, LeaveYearlyBalance balance) {
        List<CalendarEventLeaveAllocation> allocations =
            allocationRepository.findAllByEvent_IdOrderByIdAsc(event.getId());
        int cancelled = 0;
        for (CalendarEventLeaveAllocation allocation : allocations) {
            allocation.getGrant().cancel(allocation.getAllocatedMinutes());
            cancelled += allocation.getAllocatedMinutes();
        }
        allocationRepository.deleteAllByEvent_Id(event.getId());
    }

    @Transactional
    public void adjustRemaining(LeaveYearlyBalance balance, int targetRemaining) {
        LocalDate today = LocalDate.now();
        int delta = targetRemaining - grantRepository.sumActiveRemaining(balance.getUser().getId(), today);
        if (delta == 0) return;
        List<LeaveGrant> grants = grantRepository.findActiveForUpdate(balance.getUser().getId(), today);
        if (delta > 0) {
            grants.stream().findFirst().orElseThrow(() ->
                new ClientException(ErrorCode.LEAVE_BALANCE_NOT_FOUND)).adjust(delta);
        } else {
            int pending = -delta;
            for (LeaveGrant grant : grants) {
                if (pending == 0) break;
                int reduction = Math.min(pending, grant.getRemainingMinutes());
                grant.adjust(-reduction);
                pending -= reduction;
            }
            if (pending > 0) throw new ClientException(ErrorCode.LEAVE_MINUTES_INVALID);
        }
        balance.updateRemainingLeaveMinutes(targetRemaining);
    }

    @Transactional(readOnly = true)
    public int getRemaining(Long userId, LocalDate date) {
        return grantRepository.sumActiveRemaining(userId, date);
    }

    @Transactional(readOnly = true)
    public int getTotal(Long userId, LocalDate date) {
        return grantRepository.sumActiveTotal(userId, date);
    }

    @Transactional(readOnly = true)
    public int getUsed(Long userId, LocalDate date) {
        return grantRepository.sumActiveUsed(userId, date);
    }
}

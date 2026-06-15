package com.lawding.calendar.user.repository;

import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import java.time.LocalDate;

public interface LeaveYearlyBalanceRepositoryCustom {

    LeaveYearlyBalance findCurrentBalance(Long userId, LocalDate today);
}

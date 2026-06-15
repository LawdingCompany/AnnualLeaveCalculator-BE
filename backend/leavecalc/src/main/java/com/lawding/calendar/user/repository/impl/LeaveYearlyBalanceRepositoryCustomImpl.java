package com.lawding.calendar.user.repository.impl;

import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import com.lawding.calendar.user.repository.LeaveYearlyBalanceRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;

import static com.lawding.calendar.user.entity.QLeaveYearlyBalance.leaveYearlyBalance;

@RequiredArgsConstructor
public class LeaveYearlyBalanceRepositoryCustomImpl implements LeaveYearlyBalanceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public LeaveYearlyBalance findCurrentBalance(Long userId, LocalDate targetDate) {
        return queryFactory
            .selectFrom(leaveYearlyBalance)
            .where(
                // startDate <= targetDate (loe: less or equal)
                leaveYearlyBalance.startDate.loe(targetDate),
                // endDate >= targetDate (goe: greater or equal)
                leaveYearlyBalance.endDate.goe(targetDate),
                // 마감된 데이터인가? (isFinalized == false)
                leaveYearlyBalance.isFinalized.isFalse()
            ).fetchOne();
    }
}

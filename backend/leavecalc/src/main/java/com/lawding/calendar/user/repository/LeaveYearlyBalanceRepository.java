package com.lawding.calendar.user.repository;

import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveYearlyBalanceRepository extends JpaRepository<LeaveYearlyBalance, Long>,
    LeaveYearlyBalanceRepositoryCustom {

}

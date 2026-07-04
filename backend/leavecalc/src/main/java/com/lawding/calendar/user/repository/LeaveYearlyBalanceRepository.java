package com.lawding.calendar.user.repository;

import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveYearlyBalanceRepository extends JpaRepository<LeaveYearlyBalance, Long>,
    LeaveYearlyBalanceRepositoryCustom {

    Optional<LeaveYearlyBalance> findTopByUser_IdOrderByStartDateDesc(Long userId);

    Optional<LeaveYearlyBalance> findTopByUser_IdOrderByIdDesc(Long userId);

    List<LeaveYearlyBalance> findAllByIsFinalizedFalseAndEndDateBefore(LocalDate date);

    void deleteByUser_Id(Long userId);
}

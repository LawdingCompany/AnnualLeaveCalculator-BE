package com.lawding.calendar.user.repository;

import com.lawding.calendar.user.entity.LeaveYearlyBalance;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveYearlyBalanceRepository extends JpaRepository<LeaveYearlyBalance, Long>,
    LeaveYearlyBalanceRepositoryCustom {

    Optional<LeaveYearlyBalance> findTopByUser_IdOrderByStartDateDesc(Long userId);

    Optional<LeaveYearlyBalance> findTopByUser_IdOrderByIdDesc(Long userId);

    @Query("""
        select b.id
        from LeaveYearlyBalance b
        where b.isFinalized = false
          and b.endDate < :date
        order by b.endDate asc, b.id asc
        """)
    List<Long> findExpiredBalanceIds(@Param("date") LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select b
        from LeaveYearlyBalance b
        where b.id = :id
          and b.isFinalized = false
          and b.endDate < :date
        """)
    Optional<LeaveYearlyBalance> findExpiredBalanceForUpdate(
        @Param("id") Long id,
        @Param("date") LocalDate date
    );

    boolean existsByUser_IdAndStartDateAndEndDate(Long userId, LocalDate startDate, LocalDate endDate);

    void deleteByUser_Id(Long userId);
}

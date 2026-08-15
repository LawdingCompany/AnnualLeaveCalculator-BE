package com.lawding.calendar.user.repository;

import com.lawding.calendar.user.entity.LeaveGrant;
import com.lawding.calendar.user.enums.LeaveGrantType;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface LeaveGrantRepository extends JpaRepository<LeaveGrant, Long> {
    boolean existsByUser_IdAndSourceKey(Long userId, String sourceKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select g from LeaveGrant g
        where g.user.id = :userId and g.startDate <= :date and g.endDate >= :date
          and (g.grantedMinutes + g.adjustedMinutes - g.usedMinutes) > 0
        order by g.endDate asc, g.grantedDate asc, g.id asc
        """)
    List<LeaveGrant> findUsableForUpdate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select g from LeaveGrant g
        where g.user.id = :userId and g.startDate <= :date and g.endDate >= :date
        order by g.endDate desc, g.grantedDate desc, g.id desc
        """)
    List<LeaveGrant> findActiveForUpdate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("select coalesce(sum(g.grantedMinutes + g.adjustedMinutes - g.usedMinutes), 0) from LeaveGrant g where g.user.id = :userId and g.startDate <= :date and g.endDate >= :date")
    int sumActiveRemaining(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("select coalesce(sum(g.grantedMinutes + g.adjustedMinutes), 0) from LeaveGrant g where g.user.id = :userId and g.startDate <= :date and g.endDate >= :date")
    int sumActiveTotal(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("select coalesce(sum(g.usedMinutes), 0) from LeaveGrant g where g.user.id = :userId and g.startDate <= :date and g.endDate >= :date")
    int sumActiveUsed(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("select coalesce(sum(g.grantedMinutes + g.adjustedMinutes), 0) from LeaveGrant g where g.user.id = :userId and g.grantType = :type and g.startDate <= :date and g.endDate >= :date")
    int sumActiveGrantedByType(@Param("userId") Long userId, @Param("type") LeaveGrantType type,
        @Param("date") LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select g from LeaveGrant g where g.balance.id = :balanceId
        order by g.endDate desc, g.grantedDate desc, g.id desc
        """)
    List<LeaveGrant> findByBalanceIdForUpdate(@Param("balanceId") Long balanceId);

    @Query("select coalesce(sum(g.grantedMinutes + g.adjustedMinutes - g.usedMinutes), 0) from LeaveGrant g where g.balance.id = :balanceId")
    int sumRemainingByBalanceId(@Param("balanceId") Long balanceId);

    void deleteByUser_Id(Long userId);
}

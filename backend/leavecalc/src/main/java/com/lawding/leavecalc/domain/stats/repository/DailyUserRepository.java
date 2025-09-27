package com.lawding.leavecalc.domain.stats.repository;

import com.lawding.leavecalc.domain.stats.entity.DailyUser;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyUserRepository extends JpaRepository<DailyUser, LocalDate> {
}

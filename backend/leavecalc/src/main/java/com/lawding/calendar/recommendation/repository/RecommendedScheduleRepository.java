package com.lawding.calendar.recommendation.repository;

import com.lawding.calendar.recommendation.entity.RecommendedSchedule;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendedScheduleRepository extends JpaRepository<RecommendedSchedule, Long> {

    List<RecommendedSchedule> findAllByOrderByStartDateAscIdAsc();

    Optional<RecommendedSchedule> findFirstByEndDateGreaterThanEqualOrderByStartDateAscIdAsc(
        LocalDate date
    );
}

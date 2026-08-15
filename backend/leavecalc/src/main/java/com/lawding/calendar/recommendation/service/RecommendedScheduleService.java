package com.lawding.calendar.recommendation.service;

import com.lawding.calendar.recommendation.dto.request.RecommendedScheduleRequest;
import com.lawding.calendar.recommendation.entity.RecommendedSchedule;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecommendedScheduleService {

    Optional<RecommendedSchedule> findNearest(LocalDate currentDate);

    List<RecommendedSchedule> findAll();

    RecommendedSchedule findById(Long id);

    RecommendedSchedule create(RecommendedScheduleRequest request);

    RecommendedSchedule update(Long id, RecommendedScheduleRequest request);

    void delete(Long id);
}

package com.lawding.calendar.recommendation.service.impl;

import com.lawding.calendar.recommendation.dto.request.RecommendedScheduleRequest;
import com.lawding.calendar.recommendation.entity.RecommendedSchedule;
import com.lawding.calendar.recommendation.repository.RecommendedScheduleRepository;
import com.lawding.calendar.recommendation.service.RecommendedScheduleService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendedScheduleServiceImpl implements RecommendedScheduleService {

    private final RecommendedScheduleRepository recommendedScheduleRepository;

    @Override
    public Optional<RecommendedSchedule> findNearest(LocalDate currentDate) {
        return recommendedScheduleRepository
            .findFirstByEndDateGreaterThanEqualOrderByStartDateAscIdAsc(currentDate);
    }

    @Override
    public List<RecommendedSchedule> findAll() {
        return recommendedScheduleRepository.findAllByOrderByStartDateAscIdAsc();
    }

    @Override
    public RecommendedSchedule findById(Long id) {
        return recommendedScheduleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("추천 일정을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public RecommendedSchedule create(RecommendedScheduleRequest request) {
        return recommendedScheduleRepository.save(
            RecommendedSchedule.create(request.name(), request.startDate(), request.endDate())
        );
    }

    @Override
    @Transactional
    public RecommendedSchedule update(Long id, RecommendedScheduleRequest request) {
        RecommendedSchedule schedule = findById(id);
        schedule.change(request.name(), request.startDate(), request.endDate());
        return schedule;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RecommendedSchedule schedule = findById(id);
        recommendedScheduleRepository.delete(schedule);
    }
}

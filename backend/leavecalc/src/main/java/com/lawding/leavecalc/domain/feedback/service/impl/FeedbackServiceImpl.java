package com.lawding.leavecalc.domain.feedback.service.impl;

import com.lawding.leavecalc.domain.feedback.dto.FeedbackDto;
import com.lawding.leavecalc.domain.feedback.dto.request.FeedbackRequest;
import com.lawding.leavecalc.domain.feedback.entity.Feedback;
import com.lawding.leavecalc.domain.feedback.repository.FeedbackRepository;
import com.lawding.leavecalc.domain.feedback.service.FeedbackService;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Async("feedbackExecutor")
    @Transactional
    @Override
    public void createFeedback(FeedbackRequest request, Platform platform, Boolean isTest) {
        Feedback feedback = Feedback.builder()
            .platform(platform)
            .type(request.type())
            .content(request.content())
            .email(request.email())
            .rating(request.rating())
            .calculationId(request.calculationId())
            .build();
        if (Boolean.TRUE.equals(isTest)) {
            log.debug("테스트 호출 감지 -> DB 저장 생략");
            return;
        }
        feedbackRepository.save(feedback);
        log.debug("피드백 저장 완료 : {}", feedback.getId());
    }

    @Override
    public List<FeedbackDto> getAllFeedback() {
        return feedbackRepository.findAll().stream()
            .map(FeedbackDto::from)
            .toList();
    }
}

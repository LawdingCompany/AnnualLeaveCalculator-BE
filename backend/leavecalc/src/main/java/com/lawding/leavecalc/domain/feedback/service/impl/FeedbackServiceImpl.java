package com.lawding.leavecalc.domain.feedback.service.impl;

import com.lawding.leavecalc.domain.feedback.dto.request.FeedbackRequest;
import com.lawding.leavecalc.domain.feedback.entity.Feedback;
import com.lawding.leavecalc.domain.feedback.repository.FeedbackRepository;
import com.lawding.leavecalc.domain.feedback.service.FeedbackService;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Transactional
    @Override
    public void createFeedback(FeedbackRequest request, Platform platform) {
        Feedback feedback = Feedback.builder()
            .platform(platform)
            .type(request.type())
            .content(request.content())
            .email(request.email())
            .rating(request.rating())
            .calculationId(request.calculationId())
            .build();

        feedbackRepository.save(feedback);
    }
}

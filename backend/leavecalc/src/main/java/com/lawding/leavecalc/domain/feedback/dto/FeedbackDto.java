package com.lawding.leavecalc.domain.feedback.dto;

import com.lawding.leavecalc.domain.feedback.entity.Feedback;
import com.lawding.leavecalc.domain.feedback.entity.FeedbackStatus;
import com.lawding.leavecalc.domain.feedback.entity.FeedbackType;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FeedbackDto(
    Long id,
    Platform platform,
    FeedbackType type,
    String content,
    String email,
    Integer rating,
    String calculationId,
    FeedbackStatus status,
    LocalDateTime createdAt
) {
    public static FeedbackDto from(Feedback feedback) {
        return FeedbackDto.builder()
            .id(feedback.getId())
            .platform(feedback.getPlatform())
            .type(feedback.getType())
            .content(feedback.getContent())
            .email(feedback.getEmail())
            .rating(feedback.getRating())
            .calculationId(feedback.getCalculationId())
            .status(feedback.getStatus())
            .createdAt(feedback.getCreatedAt())
            .build();
    }
}

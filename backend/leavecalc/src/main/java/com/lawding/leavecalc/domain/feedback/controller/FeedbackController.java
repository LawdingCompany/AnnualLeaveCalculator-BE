package com.lawding.leavecalc.domain.feedback.controller;

import com.lawding.leavecalc.domain.feedback.dto.request.FeedbackRequest;
import com.lawding.leavecalc.domain.feedback.service.FeedbackService;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import com.lawding.leavecalc.domain.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> createFeedback(
        @Valid @RequestBody FeedbackRequest request,
        @RequestHeader(value = "X-Platform") Platform platform) {
        log.info("피드백 요청 수신 : {}, platform={}", request, platform);
        feedbackService.createFeedback(request, platform);
        return ResponseEntity.ok(ApiResponse.okMessage("피드백이 등록되었습니다."));
    }
}

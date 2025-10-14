package com.lawding.leavecalc.domain.feedback.controller;

import com.lawding.leavecalc.domain.feedback.dto.FeedbackDto;
import com.lawding.leavecalc.domain.feedback.dto.request.FeedbackRequest;
import com.lawding.leavecalc.domain.feedback.service.FeedbackService;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import com.lawding.leavecalc.domain.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getAllFeedback(){
        List<FeedbackDto> feedbackList = feedbackService.getAllFeedback();
        log.info("피드백 전체 조회: {}건", feedbackList.size());
        return ResponseEntity.ok(ApiResponse.ok(feedbackList));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createFeedback(
        @Valid @RequestBody FeedbackRequest request,
        @RequestHeader(value = "X-Platform") Platform platform,
        @RequestHeader(value = "X-Test", required = false) Boolean isTest) {
        log.debug("피드백 요청 수신 : {}, platform = {} isTest = {}", request, platform, isTest);
        feedbackService.createFeedback(request, platform, isTest);
        return ResponseEntity.ok(ApiResponse.okMessage("피드백이 등록되었습니다."));
    }
}

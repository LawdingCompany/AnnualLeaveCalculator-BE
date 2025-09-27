package com.lawding.leavecalc.domain.feedback.service;

import com.lawding.leavecalc.domain.feedback.dto.FeedbackDto;
import com.lawding.leavecalc.domain.feedback.dto.request.FeedbackRequest;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import java.util.List;

public interface FeedbackService {

    void createFeedback(FeedbackRequest request, Platform platform, Boolean isTest);

    List<FeedbackDto> getAllFeedback();
}

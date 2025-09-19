package com.lawding.leavecalc.domain.feedback.service;

import com.lawding.leavecalc.domain.feedback.dto.request.FeedbackRequest;
import com.lawding.leavecalc.domain.global.common.enums.Platform;

public interface FeedbackService {

    void createFeedback(FeedbackRequest request, Platform platform);
}

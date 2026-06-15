package com.lawding.feedback.service;

import com.lawding.feedback.dto.FeedbackDto;
import com.lawding.feedback.dto.request.FeedbackRequest;
import com.lawding.global.common.enums.Platform;
import java.util.List;

public interface FeedbackService {

    void createFeedback(FeedbackRequest request, Platform platform, Boolean isTest);

    List<FeedbackDto> getAllFeedback();
}

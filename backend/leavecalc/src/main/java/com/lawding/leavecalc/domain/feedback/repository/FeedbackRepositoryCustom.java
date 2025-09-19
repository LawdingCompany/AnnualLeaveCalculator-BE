package com.lawding.leavecalc.domain.feedback.repository;

import com.lawding.leavecalc.domain.feedback.entity.Feedback;
import com.lawding.leavecalc.domain.feedback.entity.FeedbackType;
import java.util.List;

public interface FeedbackRepositoryCustom {
    List<Feedback> searchByType(FeedbackType type);

}

package com.lawding.leavecalc.domain.feedback.repository;

import com.lawding.leavecalc.domain.feedback.entity.Feedback;
import com.lawding.leavecalc.domain.feedback.entity.FeedbackType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<Feedback> searchByType(FeedbackType type) {
        return null;
    }
}

package com.lawding.leavecalc.domain.feedback.repository;

import com.lawding.leavecalc.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long>, FeedbackRepositoryCustom {

}

package com.lawding.leavecalc.domain.feedback.entity;

import com.lawding.leavecalc.domain.global.common.enums.Platform;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)  // 애플리케이션 단에서 생성 시간 주입
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FeedbackType type;

    @Column(nullable = false, length = 1000)
    private String content;

    private String email;
    private Integer rating;

    @Column(name = "calculation_id", length = 36)
    private String calculationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FeedbackStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Feedback(Platform platform, FeedbackType type, String content, String email,
        Integer rating,
        String calculationId) {
        this.platform = platform;
        this.type = type;
        this.content = content;
        this.email = email;
        this.rating = rating;
        this.calculationId = calculationId;
        this.status = FeedbackStatus.RECEIVED;
    }

    public void updateStatus(FeedbackStatus newStatus) {
        this.status = newStatus;
    }

}

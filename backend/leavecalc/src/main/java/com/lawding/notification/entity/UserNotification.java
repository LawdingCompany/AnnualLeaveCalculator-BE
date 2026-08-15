package com.lawding.notification.entity;

import com.lawding.auth.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_notifications", uniqueConstraints =
    @UniqueConstraint(name = "uk_user_notifications_event_key", columnNames = {"user_id", "event_key"}))
public class UserNotification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, length = 50)
    private String type;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false, length = 500)
    private String message;
    private Integer grantedMinutes;
    @Column(name = "event_key", nullable = false, length = 150)
    private String eventKey;
    @Column(nullable = false)
    private Boolean isRead = false;
    private LocalDateTime readAt;
    @CreatedDate @Column(updatable = false)
    private LocalDateTime createdAt;

    private UserNotification(User user, String type, String title, String message,
        Integer grantedMinutes, String eventKey) {
        this.user = user; this.type = type; this.title = title; this.message = message;
        this.grantedMinutes = grantedMinutes; this.eventKey = eventKey;
    }
    public static UserNotification monthlyGrant(User user, int minutes, String eventKey) {
        return new UserNotification(user, "MONTHLY_LEAVE_GRANTED", "월차가 추가되었어요",
            "한 달을 채워 월차 1개가 추가되었어요!", minutes, eventKey);
    }
    public void markRead() { isRead = true; readAt = LocalDateTime.now(); }
}

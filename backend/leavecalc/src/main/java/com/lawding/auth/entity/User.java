package com.lawding.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 PK 생성
    private Long id;

    private String username;    // 소셜 로그인 사용자명
    private String email;
    private String provider;    // 로그인 제공자 (GOOGLE, KAKAO, NAVER)
    private String nickname;    // 서비스 내 표시되는 닉네임

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    @Column(length = 512)
    private String refreshToken;

    @Column(nullable = false)
    private Boolean onboardingCompleted = false;

    @Column(nullable = false)
    private Boolean deleted = false;

    private LocalDateTime deletedAt;

    private LocalDateTime hardDeleteScheduledAt;

    @Builder
    public User(String username, String email, String provider) {
        this.username = username;
        this.email = email;
        this.provider = provider;
        this.lastLoginAt = LocalDateTime.now();
    }

    public User updateUsername(String username) {
        this.username = username;
        return this;
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfile(String username, String email, String provider, String nickname) {
        if (username != null) {
            this.username = username;
        }
        if (email != null) {
            this.email = email;
        }
        if (provider != null) {
            this.provider = provider;
        }
        if (nickname != null) {
            this.nickname = nickname;
        }
    }

    public void completeOnboarding() {
        this.onboardingCompleted = true;
    }

    public void softDelete(LocalDateTime deletedAt) {
        this.deleted = true;
        this.deletedAt = deletedAt;
        this.hardDeleteScheduledAt = deletedAt.plusDays(30);
        this.refreshToken = null;
    }

    public void cancelSoftDelete() {
        this.deleted = false;
        this.deletedAt = null;
        this.hardDeleteScheduledAt = null;
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(this.deleted);
    }
}

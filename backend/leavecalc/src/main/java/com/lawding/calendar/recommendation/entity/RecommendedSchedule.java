package com.lawding.calendar.recommendation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "recommended_schedules",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_recommended_schedules_name_period",
        columnNames = {"name", "start_date", "end_date"}
    )
)
public class RecommendedSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private RecommendedSchedule(String name, LocalDate startDate, LocalDate endDate) {
        change(name, startDate, endDate);
    }

    public static RecommendedSchedule create(String name, LocalDate startDate, LocalDate endDate) {
        return new RecommendedSchedule(name, startDate, endDate);
    }

    public void change(String name, LocalDate startDate, LocalDate endDate) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("추천 일정 이름은 필수입니다.");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("추천 일정 시작일과 종료일은 필수입니다.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("추천 일정 시작일은 종료일보다 이후일 수 없습니다.");
        }
        this.name = name.trim();
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

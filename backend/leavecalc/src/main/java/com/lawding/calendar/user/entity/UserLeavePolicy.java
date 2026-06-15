package com.lawding.calendar.user.entity;

import com.lawding.auth.entity.User;
import com.lawding.calendar.user.dto.WorkPattern;
import com.lawding.calendar.user.enums.LeaveAccrualBasis;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_leave_policies")
public class UserLeavePolicy {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime acceptedAt;   // 약관 동의 시각

    @Enumerated(EnumType.STRING)
    private LeaveAccrualBasis leaveAccrualBasis;    // 연차 산정 기준 (HIRE_DATE, FISCAL_YEAR)

    private LocalDate joinDate; // 입사일
    private Integer fiscalYearBaseMonth;    // 회계연도 시작 월
    private Integer companySize;    // 상시 근로자 수

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private WorkPattern workPattern; // 요일별 근무 패턴 정보

    private LocalDate nextLeaveAccrualDate; // 다음 연차 발생 예정일(배치 작업용)

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public UserLeavePolicy(User user, LocalDateTime acceptedAt, LeaveAccrualBasis leaveAccrualBasis,
        LocalDate joinDate, Integer fiscalYearBaseMonth, Integer companySize,
        WorkPattern  workPattern, LocalDate nextLeaveAccrualDate) {
        this.user = user;
        this.acceptedAt = acceptedAt;
        this.leaveAccrualBasis = leaveAccrualBasis;
        this.joinDate = joinDate;
        this.fiscalYearBaseMonth = fiscalYearBaseMonth;
        this.companySize = companySize;
        this.workPattern = workPattern;
        this.nextLeaveAccrualDate = nextLeaveAccrualDate;
    }

    // ✔ 핵심 팩토리 메서드
    public static UserLeavePolicy create(User user,
        LocalDateTime acceptedAt,
        LeaveAccrualBasis basis,
        LocalDate joinDate,
        Integer fiscalYearBaseMonth,
        Integer companySize,
        WorkPattern workPattern,
        LocalDate nextLeaveAccrualDate) {

        return UserLeavePolicy.builder()
            .user(user)
            .acceptedAt(acceptedAt)
            .leaveAccrualBasis(basis)
            .joinDate(joinDate)
            .fiscalYearBaseMonth(fiscalYearBaseMonth)
            .companySize(companySize)
            .workPattern(workPattern)
            .nextLeaveAccrualDate(nextLeaveAccrualDate)
            .build();
    }

    public void updateWorkPattern(WorkPattern  workPattern) {
        this.workPattern = workPattern;
    }

    public void updateNextLeaveAccrualDate(LocalDate nextLeaveAccrualDate) {
        this.nextLeaveAccrualDate = nextLeaveAccrualDate;
    }
}

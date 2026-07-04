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

    private LocalDateTime acceptedAt;

    @Enumerated(EnumType.STRING)
    private LeaveAccrualBasis leaveAccrualBasis;

    private LocalDate hireDate;
    private Integer fiscalYearBaseMonth;
    private Integer companySize;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private WorkPattern workPattern;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private WorkPattern breakTimePattern;

    private LocalDate nextLeaveAccrualDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public UserLeavePolicy(
        User user,
        LocalDateTime acceptedAt,
        LeaveAccrualBasis leaveAccrualBasis,
        LocalDate hireDate,
        Integer fiscalYearBaseMonth,
        Integer companySize,
        WorkPattern workPattern,
        WorkPattern breakTimePattern,
        LocalDate nextLeaveAccrualDate
    ) {
        this.user = user;
        this.acceptedAt = acceptedAt;
        this.leaveAccrualBasis = leaveAccrualBasis;
        this.hireDate = hireDate;
        this.fiscalYearBaseMonth = fiscalYearBaseMonth;
        this.companySize = companySize;
        this.workPattern = workPattern;
        this.breakTimePattern = breakTimePattern;
        this.nextLeaveAccrualDate = nextLeaveAccrualDate;
    }

    public static UserLeavePolicy create(
        User user,
        LocalDateTime acceptedAt,
        LeaveAccrualBasis basis,
        LocalDate hireDate,
        Integer fiscalYearBaseMonth,
        Integer companySize,
        WorkPattern workPattern,
        WorkPattern breakTimePattern,
        LocalDate nextLeaveAccrualDate
    ) {
        return UserLeavePolicy.builder()
            .user(user)
            .acceptedAt(acceptedAt)
            .leaveAccrualBasis(basis)
            .hireDate(hireDate)
            .fiscalYearBaseMonth(fiscalYearBaseMonth)
            .companySize(companySize)
            .workPattern(workPattern)
            .breakTimePattern(breakTimePattern)
            .nextLeaveAccrualDate(nextLeaveAccrualDate)
            .build();
    }

    public void update(
        LocalDateTime acceptedAt,
        LeaveAccrualBasis basis,
        LocalDate hireDate,
        Integer fiscalYearBaseMonth,
        Integer companySize,
        WorkPattern workPattern,
        WorkPattern breakTimePattern,
        LocalDate nextLeaveAccrualDate
    ) {
        this.acceptedAt = acceptedAt;
        this.leaveAccrualBasis = basis;
        this.hireDate = hireDate;
        this.fiscalYearBaseMonth = fiscalYearBaseMonth;
        this.companySize = companySize;
        this.workPattern = workPattern;
        this.breakTimePattern = breakTimePattern;
        this.nextLeaveAccrualDate = nextLeaveAccrualDate;
    }

    public void updateWorkPattern(WorkPattern workPattern) {
        this.workPattern = workPattern;
    }

    public void updateNextLeaveAccrualDate(LocalDate nextLeaveAccrualDate) {
        this.nextLeaveAccrualDate = nextLeaveAccrualDate;
    }
}

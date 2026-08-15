package com.lawding.calendar.user.entity;

import com.lawding.auth.entity.User;
import com.lawding.calendar.user.enums.LeaveGrantSource;
import com.lawding.calendar.user.enums.LeaveGrantType;
import jakarta.persistence.*;
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
@Table(name = "leave_grants", uniqueConstraints =
    @UniqueConstraint(name = "uk_leave_grants_source_key", columnNames = {"user_id", "source_key"}))
public class LeaveGrant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "leave_yearly_balance_id", nullable = false)
    private LeaveYearlyBalance balance;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private LeaveGrantType grantType;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private LeaveGrantSource source;
    @Column(name = "source_key", nullable = false, length = 120)
    private String sourceKey;
    @Column(nullable = false)
    private Integer grantedMinutes;
    @Column(nullable = false)
    private Integer adjustedMinutes = 0;
    @Column(nullable = false)
    private Integer usedMinutes = 0;
    @Column(nullable = false)
    private LocalDate grantedDate;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @CreatedDate @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LeaveGrant(User user, LeaveYearlyBalance balance, LeaveGrantType grantType,
        LeaveGrantSource source, String sourceKey, int grantedMinutes, int usedMinutes,
        LocalDate grantedDate, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.balance = balance;
        this.grantType = grantType;
        this.source = source;
        this.sourceKey = sourceKey;
        this.grantedMinutes = grantedMinutes;
        this.usedMinutes = usedMinutes;
        this.grantedDate = grantedDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static LeaveGrant create(User user, LeaveYearlyBalance balance, LeaveGrantType type,
        LeaveGrantSource source, String sourceKey, int grantedMinutes, int usedMinutes,
        LocalDate grantedDate, LocalDate startDate, LocalDate endDate) {
        return new LeaveGrant(user, balance, type, source, sourceKey, grantedMinutes, usedMinutes,
            grantedDate, startDate, endDate);
    }

    public int getRemainingMinutes() {
        return grantedMinutes + adjustedMinutes - usedMinutes;
    }

    public void use(int minutes) {
        if (minutes < 0 || minutes > getRemainingMinutes()) throw new IllegalArgumentException("Invalid grant usage");
        usedMinutes += minutes;
    }

    public void cancel(int minutes) {
        if (minutes < 0 || minutes > usedMinutes) throw new IllegalArgumentException("Invalid grant cancellation");
        usedMinutes -= minutes;
    }

    public void adjust(int minutes) {
        if ((long) getRemainingMinutes() + minutes < 0) throw new IllegalArgumentException("Invalid grant adjustment");
        adjustedMinutes = Math.addExact(adjustedMinutes, minutes);
    }
}

package com.lawding.leavecalc.domain.stats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyUser {

    @Id
    @Column(name = "record_date")
    private LocalDate recordDate;

    private int web;
    private int ios;

}

package com.lawding.global.common.dto;

import java.time.LocalDate;

public record DatePeriod(
    LocalDate startDate,
    LocalDate endDate
) {}

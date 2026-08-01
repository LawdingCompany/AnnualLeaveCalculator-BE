package com.lawding.calendar.user.dto.response;

import java.math.BigDecimal;

public record DashboardResponse(
    String nickname,
    Integer remainingLeaveMinutes,
    BigDecimal avgDailyWorkHours
) {

}

package com.lawding.calendar.user.dto.response;

public record UserContextResponse(
    UserResponse user,
    UserLeavePolicyResponse leavePolicy,
    LeaveYearlyBalanceResponse leaveBalance
) {
}

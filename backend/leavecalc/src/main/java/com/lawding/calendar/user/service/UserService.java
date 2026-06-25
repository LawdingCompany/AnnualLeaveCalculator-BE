package com.lawding.calendar.user.service;

import com.lawding.calendar.user.dto.request.UserLeavePolicyRequest;
import com.lawding.calendar.user.dto.response.DashboardResponse;

public interface UserService {

    DashboardResponse getDashBoard(Long userId);

    void saveUserLeavePolicy(Long userId, UserLeavePolicyRequest request);
}

package com.lawding.calendar.user.service;

import com.lawding.calendar.user.dto.request.UserRequest;
import com.lawding.calendar.user.dto.request.UserLeavePolicyRequest;
import com.lawding.calendar.user.dto.response.DashboardResponse;
import com.lawding.calendar.user.dto.response.LeaveDashboardResponse;
import com.lawding.calendar.user.dto.response.LeaveYearlyBalanceResponse;
import com.lawding.calendar.user.dto.response.UserLeavePolicyResponse;
import com.lawding.calendar.user.dto.response.UserResponse;

public interface UserService {

    DashboardResponse getDashBoard(Long userId);

    UserResponse getUser(Long userId);

    UserResponse updateUser(Long userId, UserRequest request);

    void deleteUser(Long userId);

    UserLeavePolicyResponse getUserLeavePolicy(Long userId);

    void saveUserLeavePolicy(Long userId, UserLeavePolicyRequest request);

    UserLeavePolicyResponse updateUserLeavePolicy(Long userId, UserLeavePolicyRequest request);

    void deleteUserLeavePolicy(Long userId);

    LeaveYearlyBalanceResponse getLatestLeaveYearlyBalance(Long userId);

    LeaveDashboardResponse getLeaveDashboard(Long userId);
}

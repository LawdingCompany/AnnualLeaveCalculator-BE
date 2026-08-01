package com.lawding.calendar.user.service;

import com.lawding.calendar.user.dto.request.UserLeavePolicyRequest;
import com.lawding.calendar.user.dto.request.UserNicknameRequest;
import com.lawding.calendar.user.dto.response.DashboardResponse;
import com.lawding.calendar.user.dto.response.LeaveDashboardResponse;
import com.lawding.calendar.user.dto.response.LeaveYearlyBalanceResponse;
import com.lawding.calendar.user.dto.response.UserContextResponse;
import com.lawding.calendar.user.dto.response.UserLeavePolicyResponse;
import com.lawding.calendar.user.dto.response.UserResponse;

public interface UserService {

    UserContextResponse getUserContext(Long userId);

    DashboardResponse getDashBoard(Long userId);

    UserResponse getUser(Long userId);

    UserResponse updateUser(Long userId, UserNicknameRequest request);

    void deleteUser(Long userId);

    UserLeavePolicyResponse getUserLeavePolicy(Long userId);

    void saveUserLeavePolicy(Long userId, UserLeavePolicyRequest request);

    UserLeavePolicyResponse updateUserLeavePolicy(Long userId, UserLeavePolicyRequest request);

    void deleteUserLeavePolicy(Long userId);

    LeaveYearlyBalanceResponse getLatestLeaveYearlyBalance(Long userId);

    LeaveYearlyBalanceResponse updateTotalLeaveMinutes(Long userId, Integer totalLeaveMinutes);

    LeaveDashboardResponse getLeaveDashboard(Long userId);
}

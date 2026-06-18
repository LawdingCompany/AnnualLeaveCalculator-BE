package com.lawding.calendar.user.service;

import com.lawding.calendar.user.dto.request.UserLeavePolicyRequest;

public interface UserService {

    void saveUserLeavePolicy(Long userId, UserLeavePolicyRequest request);
}

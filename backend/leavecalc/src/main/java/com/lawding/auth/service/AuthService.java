package com.lawding.auth.service;

import com.lawding.auth.dto.response.TokenResponse;

public interface AuthService {
    TokenResponse reissue(String refreshToken);
}

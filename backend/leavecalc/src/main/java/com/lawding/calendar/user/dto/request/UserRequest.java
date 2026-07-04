package com.lawding.calendar.user.dto.request;

public record UserRequest(
    String username,
    String email,
    String provider,
    String nickname
) {
}

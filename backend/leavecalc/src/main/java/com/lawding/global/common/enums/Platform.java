package com.lawding.global.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Platform {
    WEB("web"),
    IOS("ios"),
    ANDROID("android");

    private final String value;

    Platform(String value) {
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }

    public static boolean supports(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String normalized = value.trim();
        for (Platform platform : values()) {
            if (platform.value.equalsIgnoreCase(normalized)) {
                return true;
            }
        }
        return false;
    }
}

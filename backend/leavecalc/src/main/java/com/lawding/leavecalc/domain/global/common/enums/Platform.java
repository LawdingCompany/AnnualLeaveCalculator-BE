package com.lawding.leavecalc.domain.global.common.enums;

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
}

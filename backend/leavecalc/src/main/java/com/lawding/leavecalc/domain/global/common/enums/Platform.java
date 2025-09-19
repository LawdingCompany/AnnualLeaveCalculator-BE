package com.lawding.leavecalc.domain.global.common.enums;

public enum Platform {
    WEB("web"),
    IOS("ios"),
    OTHER("other");

    private final String label;

    Platform(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

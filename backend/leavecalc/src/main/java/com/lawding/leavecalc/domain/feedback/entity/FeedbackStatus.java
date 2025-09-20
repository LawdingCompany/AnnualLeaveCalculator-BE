package com.lawding.leavecalc.domain.feedback.entity;

public enum FeedbackStatus {
    RECEIVED("접수됨"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료");

    private final String label;

    FeedbackStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

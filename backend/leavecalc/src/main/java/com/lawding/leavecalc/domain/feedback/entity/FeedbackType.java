package com.lawding.leavecalc.domain.feedback.entity;

import static com.lawding.leavecalc.domain.global.exception.ErrorCode.FEEDBACK_TYPE_CONVERT_LABEL_ERROR;

import com.lawding.leavecalc.domain.global.exception.ApplicationException;

public enum FeedbackType {
    ERROR_REPORT("오류 제보"),
    IMPROVEMENT("개선 요청"),
    QUESTION("문의"),
    OTHER("기타");

    private final String label;

    FeedbackType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // DB -> Enum 변환용
    public static FeedbackType fromLabel(String label) {
        for (FeedbackType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new ApplicationException(FEEDBACK_TYPE_CONVERT_LABEL_ERROR);
    }
}

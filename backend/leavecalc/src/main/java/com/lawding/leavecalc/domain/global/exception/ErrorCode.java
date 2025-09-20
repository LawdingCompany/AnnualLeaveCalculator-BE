package com.lawding.leavecalc.domain.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // --- Common ---
    INVALID_INPUT(1000, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    VALIDATION_FAILED(1001, HttpStatus.BAD_REQUEST, "유효성 검사에 실패했습니다."),
    JSON_PARSE_ERROR(1002, HttpStatus.BAD_REQUEST, "요청 본문을 해석할 수 없습니다."),
    MISSING_X_PLATFORM_HEADER(1003, HttpStatus.BAD_REQUEST, "필수 헤더 X-Platform 이 누락되었습니다."),
    INVALID_PLATFORM_HEADER(1004, HttpStatus.BAD_REQUEST, "X-Platform 값이 유효하지 않습니다."),
    NOT_FOUND(1404, HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(1405, HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),
    INTERNAL_ERROR(1500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // --- Domain ---
    FEEDBACK_TYPE_CONVERT_LABEL_ERROR(3001, HttpStatus.BAD_REQUEST, "알려지지 않은 피드백 유형입니다.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    public int getCode() { return code; }
    public HttpStatus getHttpStatus() { return httpStatus; }
    public String getMessage() { return message; }

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

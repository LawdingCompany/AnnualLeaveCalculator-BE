package com.lawding.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_INPUT(1000, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    VALIDATION_FAILED(1001, HttpStatus.BAD_REQUEST, "요청 값 검증에 실패했습니다."),
    JSON_PARSE_ERROR(1002, HttpStatus.BAD_REQUEST, "요청 본문을 해석할 수 없습니다."),
    MISSING_X_PLATFORM_HEADER(1003, HttpStatus.BAD_REQUEST, "필수 헤더 X-Platform이 누락되었습니다."),
    INVALID_PLATFORM_HEADER(1004, HttpStatus.BAD_REQUEST, "X-Platform 값이 유효하지 않습니다."),
    METHOD_NOT_ALLOWED(1405, HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다."),
    RESOURCE_NOT_FOUND(1404, HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),

    UNAUTHORIZED(1401, HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_TOKEN(1402, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(1403, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    ACCESS_DENIED(1406, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    INTERNAL_ERROR(1500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    EXTERNAL_SERVICE_ERROR(1501, HttpStatus.BAD_GATEWAY, "외부 서비스 연동 중 오류가 발생했습니다."),
    SERVER_CONFIGURATION_ERROR(1502, HttpStatus.INTERNAL_SERVER_ERROR, "서버 설정 오류가 발생했습니다."),
    DATA_INTEGRITY_ERROR(1503, HttpStatus.INTERNAL_SERVER_ERROR, "서버 데이터 정합성 오류가 발생했습니다."),

    USER_NOT_FOUND(2001, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    REFRESH_TOKEN_INVALID(2002, HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    UNSUPPORTED_OAUTH_PROVIDER(2003, HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인입니다."),
    APPLE_TOKEN_INVALID(2004, HttpStatus.UNAUTHORIZED, "애플 로그인 토큰이 유효하지 않습니다."),

    LEAVE_POLICY_INVALID(2101, HttpStatus.BAD_REQUEST, "연차 정책 정보가 유효하지 않습니다."),
    LEAVE_POLICY_NOT_FOUND(2102, HttpStatus.NOT_FOUND, "연차 정책 정보를 찾을 수 없습니다."),
    CURRENT_LEAVE_BALANCE_NOT_FOUND(2103, HttpStatus.NOT_FOUND, "현재 사용 가능한 연차 정보를 찾을 수 없습니다."),
    LEAVE_BALANCE_NOT_FOUND(2104, HttpStatus.NOT_FOUND, "연차 잔액 정보를 찾을 수 없습니다."),
    LEAVE_BALANCE_FINALIZED(2105, HttpStatus.CONFLICT, "이미 마감된 연차 기간입니다."),
    LEAVE_MINUTES_INVALID(2106, HttpStatus.BAD_REQUEST, "연차 시간 값이 유효하지 않습니다."),
    LEAVE_BALANCE_NOT_ENOUGH(2107, HttpStatus.CONFLICT, "잔여 연차가 부족합니다."),

    CALENDAR_EVENT_NOT_FOUND(2201, HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다."),
    CALENDAR_EVENT_PERIOD_INVALID(2202, HttpStatus.BAD_REQUEST, "일정 기간이 유효하지 않습니다."),

    DICTIONARY_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "사전 항목을 찾을 수 없습니다."),
    DICTIONARY_CATEGORY_NOT_FOUND(3002, HttpStatus.NOT_FOUND, "사전 카테고리를 찾을 수 없습니다."),
    DICTIONARY_SEARCH_KEYWORD_INVALID(3003, HttpStatus.BAD_REQUEST, "검색어가 유효하지 않습니다."),
    DICTIONARY_CATEGORY_DUPLICATED(3004, HttpStatus.CONFLICT, "이미 존재하는 카테고리입니다."),
    DICTIONARY_DEFAULT_CATEGORY_PROTECTED(3005, HttpStatus.CONFLICT, "기본 카테고리는 수정하거나 삭제할 수 없습니다."),
    DICTIONARY_STATUS_INVALID(3006, HttpStatus.CONFLICT, "사전 항목 상태가 요청과 맞지 않습니다."),
    DICTIONARY_CONTENT_INVALID(3007, HttpStatus.BAD_REQUEST, "사전 항목 값이 유효하지 않습니다."),
    DICTIONARY_CATEGORY_INVALID(3008, HttpStatus.BAD_REQUEST, "사전 카테고리 값이 유효하지 않습니다."),
    FEEDBACK_TYPE_CONVERT_LABEL_ERROR(3101, HttpStatus.BAD_REQUEST, "알 수 없는 피드백 유형입니다."),

    APP_VERSION_POLICY_NOT_FOUND(4001, HttpStatus.NOT_FOUND, "앱 버전 정책을 찾을 수 없습니다."),
    APP_VERSION_INVALID(4002, HttpStatus.BAD_REQUEST, "앱 버전 값이 유효하지 않습니다.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}

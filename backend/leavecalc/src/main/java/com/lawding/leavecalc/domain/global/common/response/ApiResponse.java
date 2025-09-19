package com.lawding.leavecalc.domain.global.common.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(Include.NON_NULL)  // JSON 직렬화 시 null 값인 필드는 아예 제외하고 출력
public class ApiResponse<T> {

    /**
     * success | error
     */
    private final String status;

    private final String message;

    private final T data;

    private final Integer code;           // 내부 에러코드 (성공 시 null)
    private final String path;            // 요청 경로
    private final String traceId;         // 추적용(옵션)
    private final String timestamp;       // ISO-8601

    // ---- Factory methods ----
    public static <T> ApiResponse<T> ok(T data, String message) {
        return base("success", message, data, null, null, null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ok(data, "success");
    }

    public static ApiResponse<Void> okMessage(String message) {
        return base("success", message, null, null, null, null);
    }

    public static <T> ApiResponse<T> error(int code, String message, String path,
        String traceId) {
        return base("error", message, null, code, path, traceId);
    }

    private static <T> ApiResponse<T> base(
        String status, String message, T data,
        Integer code, String path, String traceId
    ) {
        return ApiResponse.<T>builder()
            .status(status)
            .message(message)
            .data(data)
            .code(code)
            .path(path)
            .traceId(traceId)
            .timestamp(OffsetDateTime.now().toString())
            .build();
    }
}

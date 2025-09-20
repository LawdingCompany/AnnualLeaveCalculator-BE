package com.lawding.leavecalc.domain.global.exception;

import com.lawding.leavecalc.domain.global.common.enums.Platform;
import com.lawding.leavecalc.domain.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 글로벌 예외 핸들러
 * <p>
 * - ApplicationException: 도메인/서비스 레벨의 명시적 에러코드 처리 - Validation, 바인딩, JSON 파싱, HTTP 메서드 등 흔한 4xx 처리
 * - DataIntegrityViolation / 그 외 예기치 못한 5xx 처리
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- ApplicationException (도메인 예외) ----
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleApplicationException(
        ApplicationException ex,
        HttpServletRequest req
    ) {
        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity
            .status(ec.getHttpStatus())
            .body(ApiResponse.error(
                ec.getCode(),
                ex.getMessage(),
                path(req),
                traceId()
            ));
    }

    // ---- javax/jakarta validation (DTO @Valid) ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpServletRequest req
    ) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + (fe.getDefaultMessage() == null ? "유효하지 않습니다."
                : fe.getDefaultMessage()))
            .collect(Collectors.joining(", "));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                ErrorCode.VALIDATION_FAILED.getCode(),
                msg.isBlank() ? ErrorCode.VALIDATION_FAILED.getMessage() : msg,
                path(req),
                traceId()
            ));
    }

    // ---- jakarta validation (파라미터 @Validated) ----
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
        ConstraintViolationException ex,
        HttpServletRequest req
    ) {
        String msg = ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.joining(", "));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                ErrorCode.VALIDATION_FAILED.getCode(),
                msg.isBlank() ? ErrorCode.VALIDATION_FAILED.getMessage() : msg,
                path(req),
                traceId()
            ));
    }

    // ---- JSON 파싱/역직렬화 실패 ----
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(
        HttpMessageNotReadableException ex,
        HttpServletRequest req
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                ErrorCode.JSON_PARSE_ERROR.getCode(),
                ErrorCode.JSON_PARSE_ERROR.getMessage(),
                path(req),
                traceId()
            ));
    }

    // ---- 지원하지 않는 HTTP 메서드 ----
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(
        HttpRequestMethodNotSupportedException ex,
        HttpServletRequest req
    ) {
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ApiResponse.error(
                ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                ErrorCode.METHOD_NOT_ALLOWED.getMessage(),
                path(req),
                traceId()
            ));
    }

    // ---- 미디어 타입 오류 (Content-Type 등) ----
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotSupported(
        HttpMediaTypeNotSupportedException ex,
        HttpServletRequest req
    ) {
        return ResponseEntity
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(ApiResponse.error(
                ErrorCode.INVALID_INPUT.getCode(),
                "지원하지 않는 Content-Type 입니다.",
                path(req),
                traceId()
            ));
    }

    // ---- DataIntegrity (유니크키/외래키 제약 등) ----
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
        DataIntegrityViolationException ex,
        HttpServletRequest req
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                ErrorCode.INVALID_INPUT.getCode(),
                "데이터 무결성 제약조건을 위반했습니다.",
                path(req),
                traceId()
            ));
    }

    // ---- 스프링의 ErrorResponseException (예: ResponseStatusException 등) ----
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiResponse<Void>> handleErrorResponse(
        ErrorResponseException ex,
        HttpServletRequest req
    ) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        HttpStatus http = status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;

        return ResponseEntity
            .status(http)
            .body(ApiResponse.error(
                // 사내 공통코드가 없으므로 INVALID_INPUT/INTERNAL 중 적절히 선택
                http.is4xxClientError() ? ErrorCode.INVALID_INPUT.getCode()
                    : ErrorCode.INTERNAL_ERROR.getCode(),
                safeMessage(ex.getMessage(), http),
                path(req),
                traceId()
            ));
    }

    // ---- 필수 헤더 누락(X-Platform) ----
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingHeader(
        MissingRequestHeaderException ex,
        HttpServletRequest req
    ) {
        if ("X-Platform".equalsIgnoreCase(ex.getHeaderName())) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                    ErrorCode.MISSING_X_PLATFORM_HEADER.getCode(),
                    ErrorCode.MISSING_X_PLATFORM_HEADER.getMessage(),
                    path(req),
                    traceId()
                ));
        }
        // 다른 헤더 누락은 일반 INVALID_INPUT으로 처리
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                ErrorCode.INVALID_INPUT.getCode(),
                "필수 헤더가 누락되었습니다: " + ex.getHeaderName(),
                path(req),
                traceId()
            ));
    }

    // ---- 헤더 값 타입/변환 오류 (예: X-Platform 값이 잘못됨) ----
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex,
        HttpServletRequest req
    ) {
        // X-Platform → Platform enum 바인딩 실패 케이스만 특화 메시지
        if (ex.getRequiredType() == Platform.class || "X-Platform".equalsIgnoreCase(ex.getName())) {
            String allowed = Arrays.stream(Platform.values())
                .map(Platform::getValue)
                .collect(Collectors.joining(", "));
            Object bad = ex.getValue();
            String msg = ErrorCode.INVALID_PLATFORM_HEADER.getMessage()
                         + " (전달값: " + bad + ", 허용값: " + allowed + ")";
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                    ErrorCode.INVALID_PLATFORM_HEADER.getCode(),
                    msg,
                    path(req),
                    traceId()
                ));
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                ErrorCode.INVALID_INPUT.getCode(),
                "요청 값의 타입이 올바르지 않습니다: " + ex.getName(),
                path(req),
                traceId()
            ));
    }

    // ---- 최후 보루 ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAny(
        Exception ex,
        HttpServletRequest req
    ) {
        // 로그는 여기서 남겨두는 걸 추천
        // log.error("Unhandled exception", ex);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(
                ErrorCode.INTERNAL_ERROR.getCode(),
                ErrorCode.INTERNAL_ERROR.getMessage(),
                path(req),
                traceId()
            ));
    }

    // ========= helpers =========

    private static String path(HttpServletRequest req) {
        return req.getRequestURI();
    }

    /**
     * traceId는 다음 우선순위로 추출: 1) MDC("traceId") 2) 헤더 X-Request-Id 3) null
     * <p>
     * — 로깅 설정 시 MDC에 traceId를 심어두면 자동으로 내려감.
     */
    private static String traceId() {
        String mdc = MDC.get("traceId");
        if (mdc != null && !mdc.isBlank()) {
            return mdc;
        }
        return null; // 필요하면 RequestContextHolder로 헤더 추출 로직 추가 가능
    }

    private static String safeMessage(String msg, HttpStatus status) {
        if (msg == null || msg.isBlank()) {
            return status.is4xxClientError()
                ? ErrorCode.INVALID_INPUT.getMessage()
                : ErrorCode.INTERNAL_ERROR.getMessage();
        }
        return msg;
    }
}

package com.lawding.leavecalc.domain.global.exception;

import com.lawding.leavecalc.domain.feedback.dto.request.FeedbackRequest;
import com.lawding.leavecalc.domain.global.common.enums.Platform;
import com.lawding.leavecalc.domain.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- ApplicationException ----
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleApplicationException(
        ApplicationException ex, HttpServletRequest req) {

        log.error("ApplicationException at uri={}, msg={}, traceId={}",
            path(req), ex.getMessage(), traceId(), ex);

        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity.status(ec.getHttpStatus())
            .body(ApiResponse.error(
                ec.getCode(), ex.getMessage(), path(req), traceId()
            ));
    }

    // ---- DTO Validation ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpServletRequest req) {

        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " +
                       (fe.getDefaultMessage() == null ? "유효하지 않습니다." : fe.getDefaultMessage()))
            .collect(Collectors.joining(", "));

        log.error("Validation failed at uri={}, errors={}, traceId={}",
            path(req), msg, traceId(), ex);

        return ResponseEntity.badRequest().body(ApiResponse.error(
            ErrorCode.VALIDATION_FAILED.getCode(),
            msg.isBlank() ? ErrorCode.VALIDATION_FAILED.getMessage() : msg,
            path(req), traceId()
        ));
    }

    // ---- 파라미터 Validation ----
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
        ConstraintViolationException ex, HttpServletRequest req) {

        String msg = ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.joining(", "));

        log.error("ConstraintViolation at uri={}, violations={}, traceId={}",
            path(req), msg, traceId(), ex);

        return ResponseEntity.badRequest().body(ApiResponse.error(
            ErrorCode.VALIDATION_FAILED.getCode(),
            msg.isBlank() ? ErrorCode.VALIDATION_FAILED.getMessage() : msg,
            path(req), traceId()
        ));
    }

    // ---- JSON 파싱 실패 ----
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(
        HttpMessageNotReadableException ex, HttpServletRequest req) {

        log.error("JsonParseError at uri={}, traceId={}", path(req), traceId(), ex);

        return ResponseEntity.badRequest().body(ApiResponse.error(
            ErrorCode.JSON_PARSE_ERROR.getCode(),
            ErrorCode.JSON_PARSE_ERROR.getMessage(),
            path(req), traceId()
        ));
    }

    // ---- DataIntegrity ----
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
        DataIntegrityViolationException ex, HttpServletRequest req) {

        log.error("DataIntegrityViolation at uri={}, traceId={}", path(req), traceId(), ex);

        return ResponseEntity.badRequest().body(ApiResponse.error(
            ErrorCode.INVALID_INPUT.getCode(),
            "데이터 무결성 제약조건을 위반했습니다.",
            path(req), traceId()
        ));
    }

    // ---- 헤더 타입/바인딩 오류 ----
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex, HttpServletRequest req) {

        if (ex.getRequiredType() == Platform.class || "X-Platform".equalsIgnoreCase(ex.getName())) {
            String allowed = Arrays.stream(Platform.values())
                .map(Platform::getValue)
                .collect(Collectors.joining(", "));
            Object bad = ex.getValue();
            String msg = ErrorCode.INVALID_PLATFORM_HEADER.getMessage()
                         + " (전달값: " + bad + ", 허용값: " + allowed + ")";

            log.error("Invalid X-Platform header at uri={}, badValue={}, traceId={}",
                path(req), bad, traceId(), ex);

            return ResponseEntity.badRequest().body(ApiResponse.error(
                ErrorCode.INVALID_PLATFORM_HEADER.getCode(),
                msg, path(req), traceId()
            ));
        }

        log.error("TypeMismatch at uri={}, param={}, traceId={}",
            path(req), ex.getName(), traceId(), ex);

        return ResponseEntity.badRequest().body(ApiResponse.error(
            ErrorCode.INVALID_INPUT.getCode(),
            "요청 값의 타입이 올바르지 않습니다: " + ex.getName(),
            path(req), traceId()
        ));
    }

    // ---- 최후 보루: 모든 Exception ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAny(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at uri={}, traceId={}", path(req), traceId(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(
                ErrorCode.INTERNAL_ERROR.getCode(),
                ErrorCode.INTERNAL_ERROR.getMessage(),
                path(req), traceId()
            ));
    }

    // ========= helpers =========
    private static String path(HttpServletRequest req) { return req.getRequestURI(); }

    private static String traceId() {
        String mdc = MDC.get("traceId");
        return (mdc != null && !mdc.isBlank()) ? mdc : null;
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

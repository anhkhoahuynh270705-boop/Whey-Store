package com.gymstore.inventory.exception;

import com.gymstore.common.dto.response.ErrorResponse;
import com.gymstore.common.exception.BusinessException;
import com.gymstore.common.exception.ConflictException;
import com.gymstore.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/** Xử lý lỗi chuẩn cho inventory-service. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(ex.getStatus(), "Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflict(ConflictException ex, HttpServletRequest req) {
        log.warn("409 {}: {}", req.getRequestURI(), ex.getMessage());
        return build(409, "Conflict", ex.getMessage(), req);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> business(BusinessException ex, HttpServletRequest req) {
        String errorName = ex.getErrorCode() != null ? ex.getErrorCode().getCode() : "Business Error";
        return build(ex.getStatus(), errorName, ex.getMessage(), req);
    }

    @ExceptionHandler(com.gymstore.common.exception.BusinessException.class)
    public ResponseEntity<ErrorResponse> commonBusiness(com.gymstore.common.exception.BusinessException ex, HttpServletRequest req) {
        String errorName = ex.getErrorCode() != null ? ex.getErrorCode().getCode() : "Business Error";
        return build(ex.getStatus(), errorName, ex.getMessage(), req);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> optimisticLock(ObjectOptimisticLockingFailureException ex, HttpServletRequest req) {
        return build(409, "Conflict", "Xung đột cập nhật tồn kho, vui lòng thử lại", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, "Bad Request", "Dữ liệu không hợp lệ", req.getRequestURI(), violations));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(403, "Forbidden", "Không có quyền truy cập", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> fallback(Exception ex, HttpServletRequest req) {
        log.error("Lỗi không xử lý tại {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return build(500, "Internal Server Error", "Lỗi hệ thống", req);
    }

    private ResponseEntity<ErrorResponse> build(int status, String error, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status, error, message, req.getRequestURI(), List.of()));
    }
}

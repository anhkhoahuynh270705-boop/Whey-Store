package com.gymstore.product.exception;

import com.gymstore.common.dto.response.ErrorResponse;
import com.gymstore.common.exception.BusinessException;
import com.gymstore.common.exception.ConflictException;
import com.gymstore.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex, HttpServletRequest req) {
        log.warn("404 {}: {}", req.getRequestURI(), ex.getMessage());
        return build(ex.getStatus(), "Not Found", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflict(ConflictException ex, HttpServletRequest req) {
        log.warn("409 {}: {}", req.getRequestURI(), ex.getMessage());
        return build(409, "Conflict", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> business(BusinessException ex, HttpServletRequest req) {
        String errorName = ex.getErrorCode() != null ? ex.getErrorCode().getCode() : "Business Error";
        return build(ex.getStatus(), errorName, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return build(400, "Bad Request", "Dữ liệu không hợp lệ", req, violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(cv -> new ErrorResponse.FieldViolation(cv.getPropertyPath().toString(), cv.getMessage()))
                .toList();
        return build(400, "Bad Request", "Tham số không hợp lệ", req, violations);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> typeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return build(400, "Bad Request", "Kiểu tham số không đúng: " + ex.getName(), req, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> dataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Data integrity: {}", ex.getMessage());
        return build(409, "Conflict", "Dữ liệu vi phạm ràng buộc (có thể trùng giá trị duy nhất)", req, List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(403, "Forbidden", "Không có quyền truy cập", req, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> fallback(Exception ex, HttpServletRequest req) {
        log.error("Lỗi không xử lý tại {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return build(500, "Internal Server Error", "Lỗi hệ thống, vui lòng thử lại sau", req, List.of());
    }

    private ResponseEntity<ErrorResponse> build(int status, String error, String message,
            HttpServletRequest req, List<ErrorResponse.FieldViolation> violations) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status, error, message, req.getRequestURI(), violations));
    }
}

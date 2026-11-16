package com.gymstore.order.exception;

import com.gymstore.common.dto.response.ErrorResponse;
import com.gymstore.common.exception.BusinessException;
import com.gymstore.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(ex.getStatus(), "Not Found", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> business(BusinessException ex, HttpServletRequest req) {
        String errorName = ex.getErrorCode() != null ? ex.getErrorCode().getCode() : "Business Error";
        return build(ex.getStatus(), errorName, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(com.gymstore.common.exception.BusinessException.class)
    public ResponseEntity<ErrorResponse> commonBusiness(com.gymstore.common.exception.BusinessException ex, HttpServletRequest req) {
        String errorName = ex.getErrorCode() != null ? ex.getErrorCode().getCode() : "Business Error";
        return build(ex.getStatus(), errorName, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldViolation> v = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldViolation(fe.getField(), fe.getDefaultMessage())).toList();
        return build(400, "Bad Request", "Dữ liệu không hợp lệ", req, v);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(403, "Forbidden", "Không có quyền", req, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> fallback(Exception ex, HttpServletRequest req) {
        log.error("Lỗi {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return build(500, "Internal Server Error", "Lỗi hệ thống", req, List.of());
    }

    private ResponseEntity<ErrorResponse> build(int status, String error, String msg,
            HttpServletRequest req, List<ErrorResponse.FieldViolation> v) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status, error, msg, req.getRequestURI(), v));
    }
}

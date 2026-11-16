package com.gymstore.notification.exception;

import com.gymstore.common.dto.response.ErrorResponse;
import com.gymstore.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(ex.getStatus(), "Not Found", ex.getMessage(), req);
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> general(Exception ex, HttpServletRequest req) {
        log.error("Unhandled error: ", ex);
        return build(500, "Internal Server Error", "Lỗi dịch vụ thông báo, vui lòng thử lại sau", req);
    }

    private ResponseEntity<ErrorResponse> build(int status, String error, String msg, HttpServletRequest req) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status, error, msg, req.getRequestURI(), List.of()));
    }
}


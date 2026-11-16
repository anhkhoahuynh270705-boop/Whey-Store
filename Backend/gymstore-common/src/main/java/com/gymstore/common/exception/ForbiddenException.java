package com.gymstore.common.exception;

/** Không có quyền truy cập tài nguyên → HTTP 403. */
public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(403, message);
    }
}

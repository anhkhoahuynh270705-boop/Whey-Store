package com.gymstore.common.exception;

/** Xung đột nghiệp vụ (trùng khóa, không đủ hàng...) → HTTP 409. */
public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(409, message);
    }
}

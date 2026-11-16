package com.gymstore.common.exception;

/** Service phụ thuộc không khả dụng → HTTP 503. */
public class ServiceUnavailableException extends BusinessException {
    public ServiceUnavailableException(String message) {
        super(503, message);
    }
}

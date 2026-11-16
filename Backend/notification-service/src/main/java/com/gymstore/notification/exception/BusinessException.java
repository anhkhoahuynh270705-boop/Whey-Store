package com.gymstore.notification.exception;

/**
 * Lỗi nghiệp vụ chuyên biệt cho Notification Service.
 */
public class BusinessException extends com.gymstore.common.exception.BusinessException {

    public BusinessException(int status, String message) {
        super(status, message);
    }

    public BusinessException(IErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(IErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}


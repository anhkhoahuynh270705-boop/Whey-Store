package com.gymstore.cart.exception;

/**
 * Lỗi nghiệp vụ chuyên biệt cho Cart Service.
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


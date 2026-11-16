package com.gymstore.common.exception;

/**
 * Lớp cơ sở cho các lỗi nghiệp vụ. Mỗi subclass hoặc instance chỉ ra mã HTTP
 * phù hợp.
 */
public class BusinessException extends RuntimeException {

    private final int status;
    private final IErrorCode errorCode;

    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
        this.errorCode = null;
    }

    public BusinessException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = null;
    }

    public BusinessException(IErrorCode errorCode) {
        super(errorCode != null ? errorCode.getMessage() : "Lỗi nghiệp vụ");
        this.status = errorCode != null ? errorCode.getHttpStatus() : 500;
        this.errorCode = errorCode;
    }

    public BusinessException(IErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.status = errorCode != null ? errorCode.getHttpStatus() : 500;
        this.errorCode = errorCode;
    }

    public int getStatus() {
        return status;
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}

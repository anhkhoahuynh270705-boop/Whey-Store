package com.gymstore.notification.exception.code;

import com.gymstore.notification.exception.IErrorCode;

/**
 * Danh sách mã lỗi của Notification Service (email, template, thông báo).
 */
public enum NotificationErrorCode implements IErrorCode {

    NOTIFICATION_NOT_FOUND("NTF_001", "Thông báo không tồn tại trong hệ thống", 404),
    EMAIL_SEND_FAILED("NTF_002", "Gửi email thông báo đơn hàng thất bại", 500),
    INVALID_EMAIL_RECIPIENT("NTF_003", "Địa chỉ email người nhận không hợp lệ", 400),
    EMAIL_TEMPLATE_NOT_FOUND("NTF_004", "Mẫu nội dung thông báo không tồn tại", 404),
    MAIL_SERVER_UNREACHABLE("NTF_005", "Không thể kết nối đến máy chủ gửi mail", 503);

    private final String code;
    private final String message;
    private final int httpStatus;

    NotificationErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }
}


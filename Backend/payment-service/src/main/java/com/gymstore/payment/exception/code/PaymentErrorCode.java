package com.gymstore.payment.exception.code;

import com.gymstore.payment.exception.IErrorCode;

public enum PaymentErrorCode implements IErrorCode {

    PAYMENT_NOT_FOUND("PAY_001", "Thông tin giao dịch thanh toán không tồn tại", 404),
    PAYMENT_ALREADY_PROCESSED("PAY_002", "Giao dịch này đã được xử lý hoàn tất trước đó", 409),
    PAYMENT_TRANSACTION_FAILED("PAY_003", "Thanh toán thất bại, số dư không đủ hoặc thẻ bị từ chối", 400),
    UNSUPPORTED_METHOD("PAY_004", "Phương thức thanh toán yêu cầu không được hỗ trợ", 400),
    GATEWAY_TIMEOUT("PAY_005", "Quá thời gian chờ phản hồi từ đối tác cổng thanh toán", 504),
    INVALID_AMOUNT("PAY_006", "Số tiền thanh toán phải lớn hơn 0", 400);

    private final String code;
    private final String message;
    private final int httpStatus;

    PaymentErrorCode(String code, String message, int httpStatus) {
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

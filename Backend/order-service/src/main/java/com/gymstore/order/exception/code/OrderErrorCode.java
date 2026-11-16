package com.gymstore.order.exception.code;

import com.gymstore.order.exception.IErrorCode;

/**
 * Danh sách mã lỗi của Order Service (đặt hàng, trạng thái, hủy đơn).
 */
public enum OrderErrorCode implements IErrorCode {

    ORDER_NOT_FOUND("ORD_001", "Đơn hàng không tồn tại trong hệ thống", 404),
    ORDER_CANNOT_BE_CANCELLED("ORD_002", "Đơn hàng đang ở trạng thái không được phép hủy", 400),
    INVALID_STATUS_TRANSITION("ORD_003", "Chuyển đổi trạng thái đơn hàng không hợp lệ", 400),
    ORDER_ITEMS_EMPTY("ORD_004", "Đơn hàng phải chứa ít nhất một sản phẩm", 400),
    ORDER_TOTAL_AMOUNT_INVALID("ORD_005", "Tổng giá trị đơn hàng không hợp lệ", 400),
    SHIPPING_ADDRESS_INVALID("ORD_006", "Thông tin địa chỉ giao hàng không đầy đủ hoặc không hợp lệ", 400),
    WORKFLOW_TRIGGER_FAILED("ORD_007", "Không thể khởi động quy trình xử lý đơn hàng tự động", 500),
    ORDER_ALREADY_PAID("ORD_008", "Đơn hàng này đã được thanh toán trước đó", 409),
    ORDER_ALREADY_CANCELLED("ORD_009", "Đơn hàng này đã bị hủy trước đó", 409);

    private final String code;
    private final String message;
    private final int httpStatus;

    OrderErrorCode(String code, String message, int httpStatus) {
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


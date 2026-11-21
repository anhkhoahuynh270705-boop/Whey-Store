package com.gymstore.cart.exception.code;

import com.gymstore.cart.exception.IErrorCode;

public enum CartErrorCode implements IErrorCode {

    CART_NOT_FOUND("CRT_001", "Giỏ hàng không tồn tại", 404),
    CART_ITEM_NOT_FOUND("CRT_002", "Sản phẩm không có trong giỏ hàng", 404),
    INVALID_ITEM_QUANTITY("CRT_003", "Số lượng sản phẩm trong giỏ hàng phải lớn hơn 0", 400),
    MAX_ITEM_LIMIT_EXCEEDED("CRT_004", "Vượt quá số lượng mua tối đa cho phép trên một đơn hàng", 400),
    PRODUCT_SERVICE_UNAVAILABLE("CRT_005", "Dịch vụ thông tin sản phẩm tạm thời gián đoạn", 503),
    CART_EMPTY("CRT_006", "Giỏ hàng hiện đang trống", 400);

    private final String code;
    private final String message;
    private final int httpStatus;

    CartErrorCode(String code, String message, int httpStatus) {
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


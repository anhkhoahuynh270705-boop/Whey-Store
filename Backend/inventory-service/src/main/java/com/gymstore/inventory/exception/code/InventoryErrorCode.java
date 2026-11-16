package com.gymstore.inventory.exception.code;

import com.gymstore.inventory.exception.IErrorCode;

/**
 * Danh sách mã lỗi của Inventory Service (tồn kho, nhập xuất, giữ hàng).
 */
public enum InventoryErrorCode implements IErrorCode {

    INVENTORY_ITEM_NOT_FOUND("INV_001", "Mặt hàng không tồn tại trong kho", 404),
    INSUFFICIENT_STOCK("INV_002", "Số lượng tồn kho khả dụng không đủ đáp ứng", 400),
    SKU_ALREADY_EXISTS("INV_003", "Mã quản lý kho (SKU) đã tồn tại", 409),
    INVALID_STOCK_QUANTITY("INV_004", "Số lượng điều chỉnh kho phải lớn hơn 0", 400),
    RESERVATION_NOT_FOUND("INV_005", "Không tìm thấy yêu cầu giữ hàng hoặc đã hết hạn", 404),
    OPTIMISTIC_LOCK_CONFLICT("INV_006", "Xung đột dữ liệu kho do nhiều thao tác đồng thời, vui lòng thử lại", 409),
    PRODUCT_ALREADY_HAS_INVENTORY("INV_007", "Sản phẩm này đã được khởi tạo trong kho hàng", 409);

    private final String code;
    private final String message;
    private final int httpStatus;

    InventoryErrorCode(String code, String message, int httpStatus) {
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


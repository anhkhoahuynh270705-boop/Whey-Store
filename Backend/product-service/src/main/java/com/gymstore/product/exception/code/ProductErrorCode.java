package com.gymstore.product.exception.code;

import com.gymstore.product.exception.IErrorCode;

/**
 * Danh sách mã lỗi của Product Service (sản phẩm, danh mục, thương hiệu).
 */
public enum ProductErrorCode implements IErrorCode {

    PRODUCT_NOT_FOUND("PRD_001", "Sản phẩm không tồn tại trong hệ thống", 404),
    PRODUCT_SLUG_ALREADY_EXISTS("PRD_002", "Đường dẫn định danh (slug) của sản phẩm đã tồn tại", 409),
    CATEGORY_NOT_FOUND("PRD_003", "Danh mục sản phẩm không tồn tại", 404),
    CATEGORY_SLUG_ALREADY_EXISTS("PRD_004", "Đường dẫn định danh (slug) của danh mục đã tồn tại", 409),
    CATEGORY_HAS_PRODUCTS("PRD_005", "Không thể xóa danh mục đang có sản phẩm kinh doanh", 400),
    BRAND_NOT_FOUND("PRD_006", "Thương hiệu không tồn tại", 404),
    BRAND_SLUG_ALREADY_EXISTS("PRD_007", "Đường dẫn định danh (slug) của thương hiệu đã tồn tại", 409),
    BRAND_HAS_PRODUCTS("PRD_008", "Không thể xóa thương hiệu đang có sản phẩm kinh doanh", 400),
    INVALID_PRODUCT_PRICE("PRD_009", "Giá bán sản phẩm không hợp lệ (giá sale phải nhỏ hơn giá gốc)", 400),
    PRODUCT_INACTIVE("PRD_010", "Sản phẩm hiện đang tạm ngừng kinh doanh", 400),
    PRODUCT_IMAGE_REQUIRED("PRD_011", "Sản phẩm phải có ít nhất một ảnh minh họa", 400);

    private final String code;
    private final String message;
    private final int httpStatus;

    ProductErrorCode(String code, String message, int httpStatus) {
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


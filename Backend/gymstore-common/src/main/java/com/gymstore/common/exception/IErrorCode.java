package com.gymstore.common.exception;

public interface IErrorCode {

    /**
     * Mã định danh lỗi (VD: PRD_001, ORD_002, INV_003).
     */
    String getCode();

    /**
     * Thông điệp mô tả chi tiết lỗi tiếng Việt chuẩn cho người dùng / client.
     */
    String getMessage();

    /**
     * Mã trạng thái HTTP tương ứng (VD: 400, 401, 403, 404, 409, 500).
     */
    int getHttpStatus();
}

package com.gymstore.gateway.exception.code;

import com.gymstore.gateway.exception.IErrorCode;

public enum GatewayErrorCode implements IErrorCode {

    GW_001("GW_001", "Chưa xác thực, vui lòng đăng nhập", 401),
    GW_002("GW_002", "Không có quyền truy cập tài nguyên này", 403),
    GW_003("GW_003", "Route không tồn tại", 404),
    GW_004("GW_004", "Dịch vụ đích không khả dụng", 503),
    GW_005("GW_005", "Vượt quá giới hạn số lượng request, vui lòng thử lại sau", 429),
    GW_006("GW_006", "Lỗi gateway nội bộ", 500);

    private final String code;
    private final String message;
    private final int httpStatus;

    GatewayErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getHttpStatus() { return httpStatus; }
}

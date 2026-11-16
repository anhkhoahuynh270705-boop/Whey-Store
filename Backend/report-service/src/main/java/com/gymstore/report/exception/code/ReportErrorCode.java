package com.gymstore.report.exception.code;

import com.gymstore.report.exception.IErrorCode;

public enum ReportErrorCode implements IErrorCode {

    RPT_001("RPT_001", "Khoảng thời gian báo cáo không hợp lệ", 400),
    RPT_002("RPT_002", "Định dạng xuất không hỗ trợ", 400),
    RPT_003("RPT_003", "Lỗi xuất file báo cáo", 500),
    RPT_004("RPT_004", "Không tìm thấy đơn hàng để xuất hóa đơn", 404),
    RPT_005("RPT_005", "Lỗi tạo file PDF", 500);

    private final String code;
    private final String message;
    private final int httpStatus;

    ReportErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getHttpStatus() { return httpStatus; }
}

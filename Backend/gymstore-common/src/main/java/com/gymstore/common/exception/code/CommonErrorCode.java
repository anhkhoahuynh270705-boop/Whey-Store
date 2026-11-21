package com.gymstore.common.exception.code;

import com.gymstore.common.exception.IErrorCode;

public enum CommonErrorCode implements IErrorCode {

    UNAUTHORIZED("SYS_401", "Yêu cầu đăng nhập để thực hiện thao tác", 401),
    FORBIDDEN("SYS_403", "Bạn không có quyền truy cập chức năng này", 403),
    RESOURCE_NOT_FOUND("SYS_404", "Không tìm thấy dữ liệu yêu cầu", 404),
    METHOD_NOT_ALLOWED("SYS_405", "Phương thức HTTP không được hỗ trợ", 405),
    CONFLICT("SYS_409", "Dữ liệu bị xung đột hoặc đã tồn tại", 409),
    VALIDATION_FAILED("SYS_422", "Dữ liệu đầu vào không hợp lệ", 422),
    INTERNAL_SERVER_ERROR("SYS_500", "Lỗi hệ thống nội bộ, vui lòng thử lại sau", 500),
    SERVICE_UNAVAILABLE("SYS_503", "Dịch vụ tạm thời bị gián đoạn, vui lòng thử lại sau", 503),
    GATEWAY_TIMEOUT("SYS_504", "Quá thời gian chờ phản hồi từ máy chủ", 504);

    private final String code;
    private final String message;
    private final int httpStatus;

    CommonErrorCode(String code, String message, int httpStatus) {
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


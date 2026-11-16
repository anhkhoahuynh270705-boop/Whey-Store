package com.gymstore.workflow.exception.code;

import com.gymstore.workflow.exception.IErrorCode;

public enum WorkflowErrorCode implements IErrorCode {

    WF_001("WF_001", "Process instance không tồn tại", 404),
    WF_002("WF_002", "Task không tồn tại hoặc đã hoàn thành", 404),
    WF_003("WF_003", "Không thể khởi động Camunda BPMN", 500),
    WF_004("WF_004", "Quá thời hạn thanh toán, đơn hàng tự động hủy", 408),
    WF_005("WF_005", "Lỗi thực thi service task", 500);

    private final String code;
    private final String message;
    private final int httpStatus;

    WorkflowErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getHttpStatus() { return httpStatus; }
}

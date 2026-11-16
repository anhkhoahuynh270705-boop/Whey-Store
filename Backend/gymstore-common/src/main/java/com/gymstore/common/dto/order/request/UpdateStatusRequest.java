package com.gymstore.common.dto.order.request;

/** Request nội bộ từ workflow-service → order-service để cập nhật trạng thái + ghi history. */
public record UpdateStatusRequest(String status, String note) {}

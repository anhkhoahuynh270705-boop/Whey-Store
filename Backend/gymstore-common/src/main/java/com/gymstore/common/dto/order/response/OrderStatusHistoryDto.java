package com.gymstore.common.dto.order.response;

import java.time.LocalDateTime;

public record OrderStatusHistoryDto(String status, String note, LocalDateTime createdAt) {}

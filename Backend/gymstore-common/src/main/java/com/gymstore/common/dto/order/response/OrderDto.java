package com.gymstore.common.dto.order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        String orderNumber,
        String userId,
        String status,
        BigDecimal totalAmount,
        String shippingAddress,
        String paymentMethod,
        String processInstanceId,
        String cancelReason,
        List<OrderItemDto> items,
        List<OrderStatusHistoryDto> statusHistory,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt,
        LocalDateTime cancelledAt
) {}

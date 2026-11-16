package com.gymstore.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Sự kiện đơn bị hủy → notification gửi mail, report ghi nhận. */
public record OrderCancelledEvent(
        String orderId,
        String orderNumber,
        String userId,
        String email,
        String fullName,
        List<OrderItemEvent> items,
        BigDecimal totalAmount,
        String cancelReason,
        Instant cancelTime
) {}

package com.gymstore.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Sự kiện đơn hoàn tất → notification gửi mail xác nhận, report cập nhật read model. */
public record OrderCompletedEvent(
        String orderId,
        String orderNumber,
        String userId,
        String email,
        String fullName,
        List<OrderItemEvent> items,
        BigDecimal totalAmount,
        ShippingAddressEvent shippingAddress,
        String paymentMethod,
        Instant orderTime
) {}

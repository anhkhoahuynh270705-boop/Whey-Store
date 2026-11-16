package com.gymstore.common.dto.order.request;

import java.math.BigDecimal;
import java.util.List;

/** Request nội bộ từ workflow-service → order-service để tạo đơn PENDING. */
public record CreateOrderRequest(
        String userId,
        String userEmail,
        String userFullName,
        String shippingAddress,
        String paymentMethod,
        List<CheckoutItem> items,
        BigDecimal totalAmount
) {}

package com.gymstore.common.dto.order.response;

import java.math.BigDecimal;

public record OrderItemDto(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}

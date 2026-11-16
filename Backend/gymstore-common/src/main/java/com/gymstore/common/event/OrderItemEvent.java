package com.gymstore.common.event;

import java.math.BigDecimal;

/** Thông tin 1 dòng trong đơn, gửi kèm sự kiện (denormalize để consumer không cần gọi lại). */
public record OrderItemEvent(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}

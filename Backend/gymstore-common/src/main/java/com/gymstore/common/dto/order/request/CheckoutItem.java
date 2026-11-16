package com.gymstore.common.dto.order.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Một dòng sản phẩm khi checkout. Giá được xác nhận lại từ product-service ở
 * workflow.
 */
public record CheckoutItem(
        @NotNull
        Long productId,
        @NotBlank
        String productName,
        @NotNull
        @Min(1)
        Integer quantity,
        @NotNull
        BigDecimal unitPrice
        ) {}

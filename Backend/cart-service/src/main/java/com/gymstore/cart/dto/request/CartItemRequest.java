package com.gymstore.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Request thêm/sửa item: chỉ cần productId + quantity, service tự lấy giá từ product-service. */
public record CartItemRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity
) {}

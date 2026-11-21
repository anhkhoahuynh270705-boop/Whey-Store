package com.gymstore.cart.dto.response;

import java.math.BigDecimal;

/** Một dòng trong giỏ hàng. */
public record CartItem(
        Long productId,
        String name,
        BigDecimal price,
        BigDecimal salePrice,
        int quantity,
        String imageUrl
) {}

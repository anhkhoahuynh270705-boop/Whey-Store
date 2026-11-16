package com.gymstore.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** Toàn bộ giỏ hàng: danh sách item + tổng tiền + tổng số lượng. */
public record CartDto(
        String cartOwner,
        List<CartItem> items,
        BigDecimal totalAmount,
        int totalQuantity
) {}

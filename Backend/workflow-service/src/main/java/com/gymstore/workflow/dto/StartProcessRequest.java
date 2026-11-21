package com.gymstore.workflow.dto;

import com.gymstore.common.dto.order.request.CheckoutItem;

import java.util.List;

public record StartProcessRequest(
        String userId,
        String cartId,
        String paymentMethod,
        String shippingAddress,
        String email,
        String fullName,
        List<CheckoutItem> items
) {}

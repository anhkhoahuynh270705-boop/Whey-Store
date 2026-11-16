package com.gymstore.common.dto.order.response;

/** Response sau khi start process, trả về cho frontend để poll trạng thái. */
public record CheckoutResponse(
        Long orderId,
        String orderNumber,
        String processInstanceId,
        String message
) {}

package com.gymstore.order.dto.request;

import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.common.dto.order.request.ShippingAddressRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Request tạo đơn từ frontend. cartId dùng để gọi cart-service dọn giỏ sau khi đặt. */
public record CheckoutRequest(
        @NotBlank String cartId,
        @NotNull ShippingAddressRequest shippingAddress,
        @NotBlank String paymentMethod,
        @NotEmpty List<CheckoutItem> items
) {}

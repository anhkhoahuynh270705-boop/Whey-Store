package com.gymstore.common.dto.payment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProcessPaymentRequest(
        @NotBlank String orderId,
        @NotNull BigDecimal amount,
        @NotBlank String paymentMethod,
        /** true = force failure (dùng khi test luồng thanh toán thất bại). */
        boolean forceFailure
) {}

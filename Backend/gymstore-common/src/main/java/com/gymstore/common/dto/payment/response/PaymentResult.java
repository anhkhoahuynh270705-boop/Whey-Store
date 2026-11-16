package com.gymstore.common.dto.payment.response;

import java.math.BigDecimal;

public record PaymentResult(
        String paymentId,
        String orderId,
        boolean success,
        String transactionId,
        String message,
        BigDecimal amount
) {}

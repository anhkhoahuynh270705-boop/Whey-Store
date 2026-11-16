package com.gymstore.payment.service;

import com.gymstore.common.dto.payment.response.PaymentResult;
import com.gymstore.common.dto.payment.request.ProcessPaymentRequest;

/**
 * Interface for payment processing service.
 * Mock payment gateway supporting COD, BANK_TRANSFER, and MOCK_GATEWAY.
 */
public interface IPaymentService {

    /**
     * Process payment for an order.
     * Idempotent: if already processed, returns existing result.
     * 
     * Success rates:
     * - COD: 100%
     * - BANK_TRANSFER: 85%
     * - MOCK_GATEWAY: 90%
     * - forceFailure=true: 0% (for testing)
     */
    PaymentResult process(ProcessPaymentRequest request);

    /**
     * Get payment result by order ID.
     * @throws ResourceNotFoundException if payment not found
     */
    PaymentResult getByOrderId(String orderId);
}

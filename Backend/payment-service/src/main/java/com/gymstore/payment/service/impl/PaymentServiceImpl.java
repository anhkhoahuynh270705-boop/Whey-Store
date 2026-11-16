package com.gymstore.payment.service.impl;

import com.gymstore.common.exception.ResourceNotFoundException;
import com.gymstore.common.dto.payment.response.PaymentResult;
import com.gymstore.common.dto.payment.request.ProcessPaymentRequest;
import com.gymstore.payment.entity.Payment;
import com.gymstore.payment.enums.PaymentStatus;
import com.gymstore.payment.mapper.PaymentMapper;
import com.gymstore.payment.repository.PaymentRepository;
import com.gymstore.payment.service.IPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of payment service. Mock payment gateway: - COD: always
 * succeeds immediately. - BANK_TRANSFER: 85% success rate, 15% failure (random
 * for testing). - MOCK_GATEWAY: 90% success rate. - forceFailure=true: always
 * fails (for testing Boundary Timer).
 */
@Service
@Transactional
public class PaymentServiceImpl implements IPaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public PaymentResult process(ProcessPaymentRequest req) {
        // Idempotent: if already processed, return existing result
        return paymentRepository.findByOrderId(req.orderId())
                .map(paymentMapper::toPaymentResult)
                .orElseGet(() -> doProcess(req));
    }

    private PaymentResult doProcess(ProcessPaymentRequest req) {
        boolean success = determineSuccess(req);
        Payment payment = paymentMapper.toEntity(req);
        payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setTransactionId(success ? "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase() : null);
        payment.setProviderResponse(success ? "OK" : "INSUFFICIENT_FUNDS");
        payment = paymentRepository.save(payment);
        log.info("Payment for order {} - {}: {}", req.orderId(), req.paymentMethod(), payment.getStatus());
        return paymentMapper.toPaymentResult(payment);
    }

    private boolean determineSuccess(ProcessPaymentRequest req) {
        if (req.forceFailure()) {
            return false;
        }
        return switch (req.paymentMethod().toUpperCase()) {
            case "COD" ->
                true;
            case "BANK_TRANSFER" ->
                Math.random() < 0.85;
            default ->
                Math.random() < 0.90;
        };
    }

    @Override
    public PaymentResult getByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(paymentMapper::toPaymentResult)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for order", orderId));
    }
}

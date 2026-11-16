package com.gymstore.payment.controller;

import com.gymstore.common.dto.payment.response.PaymentResult;
import com.gymstore.common.dto.payment.request.ProcessPaymentRequest;
import com.gymstore.payment.service.IPaymentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Nội bộ: workflow gọi để xử lý thanh toán. */
    @PostMapping("/internal/payments/process")
    public PaymentResult process(@Valid @RequestBody ProcessPaymentRequest req) {
        return paymentService.process(req);
    }

    /** Công khai / STAFF xem trạng thái. */
    @GetMapping("/api/payments/{orderId}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public PaymentResult getByOrderId(@PathVariable String orderId) {
        return paymentService.getByOrderId(orderId);
    }
}

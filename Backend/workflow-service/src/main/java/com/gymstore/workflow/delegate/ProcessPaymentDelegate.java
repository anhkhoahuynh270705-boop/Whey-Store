package com.gymstore.workflow.delegate;

import com.gymstore.common.dto.payment.response.PaymentResult;
import com.gymstore.workflow.client.PaymentClient;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Task: Xử lý thanh toán. Gọi payment-service /internal/payments/process.
 * Lưu "paymentSuccess" vào process variable cho gateway đọc.
 */
@Component("processPaymentDelegate")
public class ProcessPaymentDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentDelegate.class);

    private final PaymentClient paymentClient;

    public ProcessPaymentDelegate(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Long orderId = (Long) execution.getVariable("orderId");
        BigDecimal totalAmount = (BigDecimal) execution.getVariable("totalAmount");
        String paymentMethod = (String) execution.getVariable("paymentMethod");
        Boolean forceFailure = (Boolean) execution.getVariableLocal("forcePaymentFailure");

        log.info("[ProcessPayment] orderId={} method={}", orderId, paymentMethod);

        PaymentResult result = paymentClient.process(
                String.valueOf(orderId), totalAmount, paymentMethod,
                Boolean.TRUE.equals(forceFailure));

        execution.setVariable("paymentSuccess", result.success());
        execution.setVariable("transactionId", result.transactionId());
        log.info("[ProcessPayment] orderId={} success={}", orderId, result.success());
    }
}

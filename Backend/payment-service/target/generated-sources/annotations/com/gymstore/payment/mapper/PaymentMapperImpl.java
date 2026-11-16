package com.gymstore.payment.mapper;

import com.gymstore.common.dto.payment.request.ProcessPaymentRequest;
import com.gymstore.common.dto.payment.response.PaymentResult;
import com.gymstore.payment.entity.Payment;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-16T13:36:11+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentResult toPaymentResult(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        String message = null;
        String orderId = null;
        String transactionId = null;
        BigDecimal amount = null;

        message = payment.getProviderResponse();
        orderId = payment.getOrderId();
        transactionId = payment.getTransactionId();
        amount = payment.getAmount();

        String paymentId = String.valueOf(payment.getId());
        boolean success = payment.getStatus() == com.gymstore.payment.enums.PaymentStatus.SUCCESS;

        PaymentResult paymentResult = new PaymentResult( paymentId, orderId, success, transactionId, message, amount );

        return paymentResult;
    }

    @Override
    public Payment toEntity(ProcessPaymentRequest req) {
        if ( req == null ) {
            return null;
        }

        Payment payment = new Payment();

        payment.setOrderId( req.orderId() );
        payment.setPaymentMethod( req.paymentMethod() );
        payment.setAmount( req.amount() );

        return payment;
    }
}

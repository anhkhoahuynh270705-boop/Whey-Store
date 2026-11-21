package com.gymstore.payment.mapper;

import com.gymstore.common.dto.payment.request.ProcessPaymentRequest;
import com.gymstore.common.dto.payment.response.PaymentResult;
import com.gymstore.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    /**
     * Payment entity → PaymentResult DTO.
     */
    @Mapping(target = "paymentId", expression = "java(String.valueOf(payment.getId()))")
    @Mapping(target = "success", expression = "java(payment.getStatus() == com.gymstore.payment.enums.PaymentStatus.SUCCESS)")
    @Mapping(target = "message", source = "providerResponse")
    PaymentResult toPaymentResult(Payment payment);

    /**
     * ProcessPaymentRequest → Payment entity mới.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "providerResponse", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(ProcessPaymentRequest req);

    /**
     * Khởi tạo Payment entity với kết quả xử lý giao dịch.
     */
    default Payment toEntity(ProcessPaymentRequest req, boolean success, String transactionId, String providerResponse) {
        Payment payment = toEntity(req);
        payment.setStatus(success ? com.gymstore.payment.enums.PaymentStatus.SUCCESS : com.gymstore.payment.enums.PaymentStatus.FAILED);
        payment.setTransactionId(transactionId);
        payment.setProviderResponse(providerResponse);
        return payment;
    }
}

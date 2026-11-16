package com.gymstore.workflow.client;

import com.gymstore.common.dto.payment.response.PaymentResult;
import com.gymstore.common.dto.payment.request.ProcessPaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(@Value("${payment-service.url:http://localhost:8085}") String url) {
        this.restClient = RestClient.builder().baseUrl(url).build();
    }

    public PaymentResult process(String orderId, BigDecimal amount, String method, boolean forceFailure) {
        return restClient.post().uri("/internal/payments/process")
                .body(new ProcessPaymentRequest(orderId, amount, method, forceFailure))
                .retrieve().body(PaymentResult.class);
    }
}

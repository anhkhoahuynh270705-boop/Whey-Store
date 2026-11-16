package com.gymstore.order.service;

import com.gymstore.common.exception.ForbiddenException;
import com.gymstore.common.exception.ServiceUnavailableException;
import com.gymstore.common.security.JwtUser;
import com.gymstore.common.dto.order.response.CheckoutResponse;
import com.gymstore.order.dto.request.CheckoutRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorkflowClient {

    private static final Logger log = LoggerFactory.getLogger(WorkflowClient.class);

    private final RestClient workflowRestClient;

    public WorkflowClient(@Value("${workflow-service.url:http://localhost:8086}") String baseUrl) {
        this.workflowRestClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public CheckoutResponse startPurchaseProcess(CheckoutRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("cartId", request.cartId());
        body.put("userId", JwtUser.userId());
        body.put("userEmail", JwtUser.email());
        body.put("userFullName", JwtUser.fullName());
        body.put("shippingAddress", request.shippingAddress().toOneLine());
        body.put("shippingAddressDetail", request.shippingAddress());
        body.put("paymentMethod", request.paymentMethod());
        body.put("items", request.items());

        try {
            return workflowRestClient.post()
                    .uri("/api/workflow/purchase/start")
                    .body(body)
                    .retrieve()
                    .body(CheckoutResponse.class);
        } catch (RestClientException ex) {
            log.error("Không thể gọi workflow-service: {}", ex.getMessage());
            throw new ServiceUnavailableException("Không thể khởi động quy trình đặt hàng, vui lòng thử lại");
        }
    }
}

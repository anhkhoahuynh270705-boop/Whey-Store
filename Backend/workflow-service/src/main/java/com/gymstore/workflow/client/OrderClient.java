package com.gymstore.workflow.client;

import com.gymstore.common.dto.order.request.CreateOrderRequest;
import com.gymstore.common.dto.order.response.OrderDto;
import com.gymstore.common.dto.order.request.UpdateStatusRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OrderClient {

    private final RestClient restClient;

    public OrderClient(@Value("${order-service.url:http://localhost:8084}") String url) {
        this.restClient = RestClient.builder().baseUrl(url).build();
    }

    public OrderDto create(CreateOrderRequest req) {
        return restClient.post().uri("/internal/orders").body(req).retrieve().body(OrderDto.class);
    }

    public OrderDto updateStatus(Long orderId, String status, String note) {
        return restClient.put().uri("/internal/orders/{id}/status", orderId)
                .body(new UpdateStatusRequest(status, note)).retrieve().body(OrderDto.class);
    }
}

package com.gymstore.workflow.client;

import com.gymstore.common.contract.ReserveRequest;
import com.gymstore.common.contract.ReserveResult;
import com.gymstore.common.contract.StockActionRequest;
import com.gymstore.common.contract.StockActionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(@Value("${inventory-service.url:http://localhost:8082}") String url) {
        this.restClient = RestClient.builder().baseUrl(url).build();
    }

    public ReserveResult reserve(ReserveRequest req) {
        return restClient.post().uri("/internal/inventory/reserve")
                .body(req).retrieve().body(ReserveResult.class);
    }

    public StockActionResult commit(String orderId) {
        return restClient.post().uri("/internal/inventory/commit")
                .body(new StockActionRequest(orderId)).retrieve().body(StockActionResult.class);
    }

    public StockActionResult release(String orderId) {
        return restClient.post().uri("/internal/inventory/release")
                .body(new StockActionRequest(orderId)).retrieve().body(StockActionResult.class);
    }
}

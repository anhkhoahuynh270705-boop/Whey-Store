package com.gymstore.cart.client;

import com.gymstore.cart.dto.response.ProductSummary;
import com.gymstore.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(RestClient productRestClient) {
        this.restClient = productRestClient;
    }

    public ProductSummary getProduct(Long productId) {
        try {
            return restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductSummary.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Sản phẩm", productId);
        }
    }
}

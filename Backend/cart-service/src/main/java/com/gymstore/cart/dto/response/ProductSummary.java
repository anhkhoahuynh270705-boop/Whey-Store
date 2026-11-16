package com.gymstore.cart.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductSummary(
        Long id,
        String name,
        BigDecimal price,
        BigDecimal salePrice,
        String status,
        List<ProductImageSummary> images
) {
    public String primaryImage() {
        if (images() != null && !images().isEmpty()) {
            return images().stream()
                    .filter(ProductImageSummary::isPrimary)
                    .map(ProductImageSummary::imageUrl)
                    .findFirst()
                    .orElse(images().get(0).imageUrl());
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductImageSummary(Long id, String imageUrl, boolean isPrimary) {}

    /** Giá áp dụng: salePrice nếu có, ngược lại price. */
    public BigDecimal effectivePrice() {
        return salePrice != null ? salePrice : price;
    }
}

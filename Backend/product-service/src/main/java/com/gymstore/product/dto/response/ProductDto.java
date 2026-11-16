package com.gymstore.product.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Chi tiết sản phẩm (response). */
public record ProductDto(
        Long id,
        String name,
        String slug,
        String description,
        Long categoryId,
        String categoryName,
        Long brandId,
        String brandName,
        BigDecimal price,
        BigDecimal salePrice,
        Integer weightGrams,
        String flavor,
        Integer servingSize,
        String status,
        List<ProductImageDto> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

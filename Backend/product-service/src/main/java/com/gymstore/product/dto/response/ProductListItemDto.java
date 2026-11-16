package com.gymstore.product.dto.response;

import java.math.BigDecimal;

/** Dòng trong danh sách sản phẩm (nhẹ hơn, dùng cho phân trang). */
public record ProductListItemDto(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        BigDecimal salePrice,
        String brandName,
        String categoryName,
        String primaryImage,
        String status
) {}

package com.gymstore.product.dto.request;

import com.gymstore.product.dto.response.ProductImageDto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/** Request tạo/cập nhật sản phẩm. */
public record ProductRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 200) String slug,
        @Size(max = 5000) String description,
        @NotNull Long categoryId,
        @NotNull Long brandId,
        @NotNull @DecimalMin("0") BigDecimal price,
        @PositiveOrZero BigDecimal salePrice,
        @Min(0) Integer weightGrams,
        String flavor,
        @Min(0) Integer servingSize,
        @NotBlank String status,
        List<ProductImageDto> images
) {}

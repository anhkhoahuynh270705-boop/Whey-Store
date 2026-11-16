package com.gymstore.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request tạo/cập nhật thương hiệu. */
public record BrandRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 200) String slug,
        @Size(max = 2000) String description,
        String logoUrl
) {}

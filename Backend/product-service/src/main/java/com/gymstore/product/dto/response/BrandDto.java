package com.gymstore.product.dto.response;

import java.time.LocalDateTime;

/** DTO thương hiệu. */
public record BrandDto(Long id, String slug, String name, String description, String logoUrl, LocalDateTime createdAt) {}

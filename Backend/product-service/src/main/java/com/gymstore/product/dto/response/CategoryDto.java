package com.gymstore.product.dto.response;

import java.time.LocalDateTime;

/** DTO danh mục. */
public record CategoryDto(Long id, String slug, String name, String description, LocalDateTime createdAt) {}

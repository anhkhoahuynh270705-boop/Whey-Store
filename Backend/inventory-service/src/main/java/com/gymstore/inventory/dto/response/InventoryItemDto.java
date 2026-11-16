package com.gymstore.inventory.dto.response;

/** Thông tin tồn kho của một sản phẩm. */
public record InventoryItemDto(
        Long id,
        Long productId,
        String sku,
        int availableQty,
        int reservedQty,
        Long version
) {}

package com.gymstore.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Request nhập/thiết lập tồn kho (dành STAFF/ADMIN). */
public record UpdateStockRequest(
        @NotBlank String sku,
        @NotNull @Min(0) Integer availableQty    // số lượng muốn đặt (set) hoặc thêm (tùy api)
) {}

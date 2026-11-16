package com.gymstore.product.dto.request;

import com.gymstore.product.enums.ProductStatus;

import java.math.BigDecimal;

/**
 * Bộ lọc sản phẩm từ query param. Các trường null = không lọc.
 *
 * @param search          tìm theo tên/mô tả
 * @param categoryId      lọc theo danh mục
 * @param brandId         lọc theo thương hiệu
 * @param flavor          lọc theo hương vị
 * @param minPrice        giá tối thiểu
 * @param maxPrice        giá tối đa
 * @param status          trạng thái (ACTIVE/INACTIVE)
 * @param includeInactive true khi admin muốn xem cả sản phẩm ngừng bán
 */
public record ProductFilter(
        String search,
        Long categoryId,
        Long brandId,
        String flavor,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        ProductStatus status,
        boolean includeInactive
) {}

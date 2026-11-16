package com.gymstore.product.service;

import com.gymstore.common.dto.response.PageResponse;
import com.gymstore.product.dto.response.ProductDto;
import com.gymstore.product.dto.request.ProductFilter;
import com.gymstore.product.dto.response.ProductListItemDto;
import com.gymstore.product.dto.request.ProductRequest;
import org.springframework.data.domain.Pageable;

public interface IProductService {
    
    /**
     * Danh sách sản phẩm (lọc, sắp xếp, phân trang).
     */
    PageResponse<ProductListItemDto> list(ProductFilter filter, Pageable pageable);
    
    /**
     * Chi tiết sản phẩm theo id.
     */
    ProductDto getById(Long id);
    
    /**
     * Chi tiết sản phẩm theo slug.
     */
    ProductDto getBySlug(String slug);
    
    /**
     * Tạo sản phẩm mới.
     */
    ProductDto create(ProductRequest request);
    
    /**
     * Cập nhật sản phẩm.
     */
    ProductDto update(Long id, ProductRequest request);
    
    /**
     * Xóa sản phẩm.
     */
    void delete(Long id);
}

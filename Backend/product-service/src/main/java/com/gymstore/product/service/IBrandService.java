package com.gymstore.product.service;

import com.gymstore.product.dto.response.BrandDto;
import com.gymstore.product.dto.request.BrandRequest;

import java.util.List;

public interface IBrandService {
    
    /**
     * Danh sách tất cả thương hiệu.
     */
    List<BrandDto> list();
    
    /**
     * Lấy thông tin thương hiệu theo id.
     */
    BrandDto getById(Long id);
    
    /**
     * Tạo thương hiệu mới.
     */
    BrandDto create(BrandRequest request);
    
    /**
     * Cập nhật thương hiệu.
     */
    BrandDto update(Long id, BrandRequest request);
    
    /**
     * Xóa thương hiệu.
     */
    void delete(Long id);
}

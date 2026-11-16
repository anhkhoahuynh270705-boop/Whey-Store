package com.gymstore.product.service;

import com.gymstore.product.dto.response.CategoryDto;
import com.gymstore.product.dto.request.CategoryRequest;

import java.util.List;

public interface ICategoryService {
    
    /**
     * Danh sách tất cả danh mục.
     */
    List<CategoryDto> list();
    
    /**
     * Lấy thông tin danh mục theo id.
     */
    CategoryDto getById(Long id);
    
    /**
     * Tạo danh mục mới.
     */
    CategoryDto create(CategoryRequest request);
    
    /**
     * Cập nhật danh mục.
     */
    CategoryDto update(Long id, CategoryRequest request);
    
    /**
     * Xóa danh mục.
     */
    void delete(Long id);
}

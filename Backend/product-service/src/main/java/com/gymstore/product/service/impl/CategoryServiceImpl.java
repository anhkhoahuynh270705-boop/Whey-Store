package com.gymstore.product.service.impl;

import com.gymstore.common.exception.ConflictException;
import com.gymstore.common.exception.ResourceNotFoundException;
import com.gymstore.product.config.RedisConfig;
import com.gymstore.product.dto.response.CategoryDto;
import com.gymstore.product.dto.request.CategoryRequest;
import com.gymstore.product.entity.Category;
import com.gymstore.product.mapper.CategoryMapper;
import com.gymstore.product.repository.CategoryRepository;
import com.gymstore.product.repository.ProductRepository;
import com.gymstore.product.service.ICategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper mapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               CategoryMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @Cacheable(cacheNames = RedisConfig.CACHE_CATEGORY_LIST)
    public List<CategoryDto> list() {
        return categoryRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisConfig.CACHE_CATEGORY_LIST, allEntries = true)
    public CategoryDto create(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.slug())) {
            throw new ConflictException("Slug danh mục đã tồn tại: " + request.slug());
        }
        Category saved = categoryRepository.save(mapper.toEntity(request));
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisConfig.CACHE_CATEGORY_LIST, allEntries = true)
    public CategoryDto update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", id));
        if (!request.slug().equals(category.getSlug()) && categoryRepository.existsBySlug(request.slug())) {
            throw new ConflictException("Slug danh mục đã tồn tại: " + request.slug());
        }
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());
        return mapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisConfig.CACHE_CATEGORY_LIST, allEntries = true)
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", id));
        if (productRepository.existsAnyProductForCategory(id)) {
            throw new ConflictException("Không thể xoá danh mục đang có sản phẩm");
        }
        categoryRepository.delete(category);
    }
}

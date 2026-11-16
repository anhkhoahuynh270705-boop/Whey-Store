package com.gymstore.product.service.impl;

import com.gymstore.common.exception.ConflictException;
import com.gymstore.common.exception.ResourceNotFoundException;
import com.gymstore.product.config.RedisConfig;
import com.gymstore.product.dto.response.BrandDto;
import com.gymstore.product.dto.request.BrandRequest;
import com.gymstore.product.entity.Brand;
import com.gymstore.product.mapper.BrandMapper;
import com.gymstore.product.repository.BrandRepository;
import com.gymstore.product.repository.ProductRepository;
import com.gymstore.product.service.IBrandService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BrandServiceImpl implements IBrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final BrandMapper mapper;

    public BrandServiceImpl(BrandRepository brandRepository,
                            ProductRepository productRepository,
                            BrandMapper mapper) {
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @Cacheable(cacheNames = RedisConfig.CACHE_BRAND_LIST)
    public List<BrandDto> list() {
        return brandRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public BrandDto getById(Long id) {
        return brandRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Thương hiệu", id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisConfig.CACHE_BRAND_LIST, allEntries = true)
    public BrandDto create(BrandRequest request) {
        if (brandRepository.existsBySlug(request.slug())) {
            throw new ConflictException("Slug thương hiệu đã tồn tại: " + request.slug());
        }
        return mapper.toDto(brandRepository.save(mapper.toEntity(request)));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisConfig.CACHE_BRAND_LIST, allEntries = true)
    public BrandDto update(Long id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thương hiệu", id));
        if (!request.slug().equals(brand.getSlug()) && brandRepository.existsBySlug(request.slug())) {
            throw new ConflictException("Slug thương hiệu đã tồn tại: " + request.slug());
        }
        brand.setName(request.name());
        brand.setSlug(request.slug());
        brand.setDescription(request.description());
        brand.setLogoUrl(request.logoUrl());
        return mapper.toDto(brandRepository.save(brand));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisConfig.CACHE_BRAND_LIST, allEntries = true)
    public void delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thương hiệu", id));
        if (productRepository.existsAnyProductForBrand(id)) {
            throw new ConflictException("Không thể xoá thương hiệu đang có sản phẩm");
        }
        brandRepository.delete(brand);
    }
}

package com.gymstore.product.service.impl;

import com.gymstore.common.dto.response.PageResponse;
import com.gymstore.common.exception.ConflictException;
import com.gymstore.common.exception.ResourceNotFoundException;
import com.gymstore.product.cache.ProductCacheHelper;
import com.gymstore.product.config.RedisConfig;
import com.gymstore.product.dto.response.ProductDto;
import com.gymstore.product.dto.request.ProductFilter;
import com.gymstore.product.dto.response.ProductListItemDto;
import com.gymstore.product.dto.request.ProductRequest;
import com.gymstore.product.entity.Brand;
import com.gymstore.product.entity.Category;
import com.gymstore.product.entity.Product;
import com.gymstore.product.mapper.ProductMapper;
import com.gymstore.product.repository.BrandRepository;
import com.gymstore.product.repository.CategoryRepository;
import com.gymstore.product.repository.ProductImageRepository;
import com.gymstore.product.repository.ProductRepository;
import com.gymstore.product.repository.ProductSpecifications;
import com.gymstore.product.service.IProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements IProductService {

    ProductRepository productRepository;
    ProductImageRepository productImageRepository;
    CategoryRepository categoryRepository;
    BrandRepository brandRepository;
    ProductMapper mapper;
    ProductCacheHelper cacheHelper;

    @Override
    @Cacheable(cacheNames = RedisConfig.CACHE_PRODUCT_LIST, keyGenerator = "pageableKeyGenerator")
    public PageResponse<ProductListItemDto> list(ProductFilter filter, Pageable pageable) {
        Page<Product> page = productRepository.findAll(ProductSpecifications.withFilters(filter), pageable);

        // Lấy ảnh đại diện một lần cho toàn bộ trang (tránh N+1)
        List<Long> ids = page.getContent().stream().map(p -> p.getId()).toList();
        Map<Long, String> primaryImages = loadPrimaryImages(ids);

        Page<ProductListItemDto> mapped = page.map(p
                -> mapper.toListDto(p, primaryImages.get(p.getId())));
        return PageResponse.from(mapped);
    }

    private Map<Long, String> loadPrimaryImages(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return productImageRepository.findPrimaryImagesByProductIdIn(ids).stream()
                .collect(Collectors.toMap(
                        ProductImageRepository.PrimaryImageProjection::getProductId,
                        ProductImageRepository.PrimaryImageProjection::getImageUrl,
                        (a, b) -> a));
    }

    @Override
    public ProductDto getById(Long id) {
        ProductDto dto = cacheHelper.getOrLoad(id, () -> productRepository.findDetailById(id).map(mapper::toDto));
        if (dto == null) {
            throw new ResourceNotFoundException("Sản phẩm", id);
        }
        return dto;
    }

    @Override
    public ProductDto getBySlug(String slug) {
        return productRepository.findDetailBySlug(slug)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", slug));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisConfig.CACHE_PRODUCT_LIST, allEntries = true)
    public ProductDto create(ProductRequest request) {
        String slug = (request.slug() != null && !request.slug().isBlank())
                ? request.slug().trim()
                : toSlug(request.name());

        if (productRepository.existsBySlug(slug)) {
            throw new ConflictException("Slug đã tồn tại: " + slug);
        }
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", request.categoryId()));
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("Thương hiệu", request.brandId()));

        Product product = mapper.toEntity(request);
        product.setSlug(slug);
        product.setCategory(category);
        product.setBrand(brand);
        if (request.images() != null) {
            request.images().forEach(img -> product.addImage(mapper.toImageEntity(img)));
        }
        Product saved = productRepository.save(product);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisConfig.CACHE_PRODUCT_LIST, allEntries = true)
    public ProductDto update(Long id, ProductRequest request) {
        Product product = productRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", id));

        String slug = (request.slug() != null && !request.slug().isBlank())
                ? request.slug().trim()
                : product.getSlug();

        if (!slug.equals(product.getSlug()) && productRepository.existsBySlug(slug)) {
            throw new ConflictException("Slug đã tồn tại: " + slug);
        }
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", request.categoryId()));
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("Thương hiệu", request.brandId()));

        mapper.updateEntityFromRequest(request, product);
        product.setSlug(slug);
        product.setCategory(category);
        product.setBrand(brand);

        if (request.images() != null) {
            product.clearImages();
            request.images().forEach(img -> product.addImage(mapper.toImageEntity(img)));
        }
        Product saved = productRepository.save(product);
        cacheHelper.evict(id);
        return mapper.toDto(saved);
    }

    private String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "san-pham-" + System.currentTimeMillis();
        }
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        String noAccents = normalized.replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "d");
        String slug = noAccents.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
        return slug.isBlank() ? "san-pham-" + System.currentTimeMillis() : slug;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisConfig.CACHE_PRODUCT_LIST, allEntries = true)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sản phẩm", id);
        }
        productRepository.deleteById(id);
        cacheHelper.evict(id);
    }
}

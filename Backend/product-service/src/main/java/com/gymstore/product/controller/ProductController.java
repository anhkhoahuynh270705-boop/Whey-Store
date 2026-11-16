package com.gymstore.product.controller;

import com.gymstore.common.dto.response.PageResponse;
import com.gymstore.product.dto.response.ProductDto;
import com.gymstore.product.dto.request.ProductFilter;
import com.gymstore.product.dto.response.ProductListItemDto;
import com.gymstore.product.dto.request.ProductRequest;
import com.gymstore.product.enums.ProductStatus;
import com.gymstore.product.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PageResponse<ProductListItemDto> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String flavor,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        ProductFilter filter = new ProductFilter(search, categoryId, brandId, flavor,
                minPrice, maxPrice, status, includeInactive);
        return productService.list(filter, pageable);
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @GetMapping("/slug/{slug}")
    public ProductDto getBySlug(@PathVariable String slug) {
        return productService.getBySlug(slug);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}

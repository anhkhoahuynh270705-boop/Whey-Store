package com.gymstore.product.controller;

import com.gymstore.product.dto.response.BrandDto;
import com.gymstore.product.dto.request.BrandRequest;
import com.gymstore.product.service.IBrandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final IBrandService brandService;

    public BrandController(IBrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public List<BrandDto> list() {
        return brandService.list();
    }

    @GetMapping("/{id}")
    public BrandDto getById(@PathVariable Long id) {
        return brandService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public BrandDto create(@Valid @RequestBody BrandRequest request) {
        return brandService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BrandDto update(@PathVariable Long id, @Valid @RequestBody BrandRequest request) {
        return brandService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        brandService.delete(id);
    }
}

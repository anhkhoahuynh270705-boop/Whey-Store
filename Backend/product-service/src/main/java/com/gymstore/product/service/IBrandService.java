package com.gymstore.product.service;

import java.util.List;

import com.gymstore.product.dto.request.BrandRequest;
import com.gymstore.product.dto.response.BrandDto;

public interface IBrandService {

    List<BrandDto> list();

    BrandDto getById(Long id);

    BrandDto create(BrandRequest request);

    BrandDto update(Long id, BrandRequest request);

    void delete(Long id);
}

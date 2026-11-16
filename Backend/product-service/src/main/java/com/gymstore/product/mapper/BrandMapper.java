package com.gymstore.product.mapper;

import com.gymstore.product.dto.response.BrandDto;
import com.gymstore.product.dto.request.BrandRequest;
import com.gymstore.product.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    BrandDto toDto(Brand brand);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Brand toEntity(BrandRequest request);
}

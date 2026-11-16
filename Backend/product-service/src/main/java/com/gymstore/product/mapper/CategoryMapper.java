package com.gymstore.product.mapper;

import com.gymstore.product.dto.response.CategoryDto;
import com.gymstore.product.dto.request.CategoryRequest;
import com.gymstore.product.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CategoryRequest request);
}

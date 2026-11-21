package com.gymstore.product.mapper;

import com.gymstore.product.dto.request.CategoryRequest;
import com.gymstore.product.dto.response.CategoryDto;
import com.gymstore.product.entity.Category;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-20T09:37:09+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDto toDto(Category category) {
        if ( category == null ) {
            return null;
        }

        Long id = null;
        String slug = null;
        String name = null;
        String description = null;
        LocalDateTime createdAt = null;

        id = category.getId();
        slug = category.getSlug();
        name = category.getName();
        description = category.getDescription();
        createdAt = category.getCreatedAt();

        CategoryDto categoryDto = new CategoryDto( id, slug, name, description, createdAt );

        return categoryDto;
    }

    @Override
    public Category toEntity(CategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Category category = new Category();

        category.setDescription( request.description() );
        category.setName( request.name() );
        category.setSlug( request.slug() );

        return category;
    }
}

package com.gymstore.product.mapper;

import com.gymstore.product.dto.request.BrandRequest;
import com.gymstore.product.dto.response.BrandDto;
import com.gymstore.product.entity.Brand;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-20T09:37:09+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class BrandMapperImpl implements BrandMapper {

    @Override
    public BrandDto toDto(Brand brand) {
        if ( brand == null ) {
            return null;
        }

        Long id = null;
        String slug = null;
        String name = null;
        String description = null;
        String logoUrl = null;
        LocalDateTime createdAt = null;

        id = brand.getId();
        slug = brand.getSlug();
        name = brand.getName();
        description = brand.getDescription();
        logoUrl = brand.getLogoUrl();
        createdAt = brand.getCreatedAt();

        BrandDto brandDto = new BrandDto( id, slug, name, description, logoUrl, createdAt );

        return brandDto;
    }

    @Override
    public Brand toEntity(BrandRequest request) {
        if ( request == null ) {
            return null;
        }

        Brand brand = new Brand();

        brand.setDescription( request.description() );
        brand.setLogoUrl( request.logoUrl() );
        brand.setName( request.name() );
        brand.setSlug( request.slug() );

        return brand;
    }
}

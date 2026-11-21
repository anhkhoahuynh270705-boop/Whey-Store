package com.gymstore.product.mapper;

import com.gymstore.product.dto.request.ProductRequest;
import com.gymstore.product.dto.response.ProductDto;
import com.gymstore.product.dto.response.ProductImageDto;
import com.gymstore.product.dto.response.ProductListItemDto;
import com.gymstore.product.entity.Brand;
import com.gymstore.product.entity.Category;
import com.gymstore.product.entity.Product;
import com.gymstore.product.entity.ProductImage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-20T09:37:09+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductImageDto toImageDto(ProductImage image) {
        if ( image == null ) {
            return null;
        }

        ProductImageDto productImageDto = new ProductImageDto();

        productImageDto.setId( image.getId() );
        productImageDto.setImageUrl( image.getImageUrl() );

        return productImageDto;
    }

    @Override
    public List<ProductImageDto> toImageDtos(List<ProductImage> images) {
        if ( images == null ) {
            return null;
        }

        List<ProductImageDto> list = new ArrayList<ProductImageDto>( images.size() );
        for ( ProductImage productImage : images ) {
            list.add( toImageDto( productImage ) );
        }

        return list;
    }

    @Override
    public ProductImage toImageEntity(ProductImageDto dto) {
        if ( dto == null ) {
            return null;
        }

        ProductImage productImage = new ProductImage();

        productImage.setImageUrl( dto.getImageUrl() );
        productImage.setPrimary( dto.isPrimary() );

        return productImage;
    }

    @Override
    public ProductDto toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        Long categoryId = null;
        String categoryName = null;
        Long brandId = null;
        String brandName = null;
        String status = null;
        Long id = null;
        String name = null;
        String slug = null;
        String description = null;
        BigDecimal price = null;
        BigDecimal salePrice = null;
        Integer weightGrams = null;
        String flavor = null;
        Integer servingSize = null;
        List<ProductImageDto> images = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        categoryId = productCategoryId( product );
        categoryName = productCategoryName( product );
        brandId = productBrandId( product );
        brandName = productBrandName( product );
        status = enumToString( product.getStatus() );
        id = product.getId();
        name = product.getName();
        slug = product.getSlug();
        description = product.getDescription();
        price = product.getPrice();
        salePrice = product.getSalePrice();
        weightGrams = product.getWeightGrams();
        flavor = product.getFlavor();
        servingSize = product.getServingSize();
        images = toImageDtos( product.getImages() );
        createdAt = product.getCreatedAt();
        updatedAt = product.getUpdatedAt();

        ProductDto productDto = new ProductDto( id, name, slug, description, categoryId, categoryName, brandId, brandName, price, salePrice, weightGrams, flavor, servingSize, status, images, createdAt, updatedAt );

        return productDto;
    }

    @Override
    public ProductListItemDto toListDto(Product product) {
        if ( product == null ) {
            return null;
        }

        String brandName = null;
        String categoryName = null;
        String status = null;
        Long id = null;
        String name = null;
        String slug = null;
        BigDecimal price = null;
        BigDecimal salePrice = null;

        brandName = productBrandName( product );
        categoryName = productCategoryName( product );
        status = enumToString( product.getStatus() );
        id = product.getId();
        name = product.getName();
        slug = product.getSlug();
        price = product.getPrice();
        salePrice = product.getSalePrice();

        String primaryImage = null;

        ProductListItemDto productListItemDto = new ProductListItemDto( id, name, slug, price, salePrice, brandName, categoryName, primaryImage, status );

        return productListItemDto;
    }

    @Override
    public Product toEntity(ProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product product = new Product();

        product.setStatus( stringToEnum( request.status() ) );
        product.setDescription( request.description() );
        product.setFlavor( request.flavor() );
        product.setName( request.name() );
        product.setPrice( request.price() );
        product.setSalePrice( request.salePrice() );
        product.setServingSize( request.servingSize() );
        product.setSlug( request.slug() );
        product.setWeightGrams( request.weightGrams() );

        return product;
    }

    @Override
    public void updateEntityFromRequest(ProductRequest request, Product product) {
        if ( request == null ) {
            return;
        }

        product.setStatus( stringToEnum( request.status() ) );
        product.setDescription( request.description() );
        product.setFlavor( request.flavor() );
        product.setName( request.name() );
        product.setPrice( request.price() );
        product.setSalePrice( request.salePrice() );
        product.setServingSize( request.servingSize() );
        product.setWeightGrams( request.weightGrams() );
    }

    private Long productCategoryId(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String productCategoryName(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long productBrandId(Product product) {
        if ( product == null ) {
            return null;
        }
        Brand brand = product.getBrand();
        if ( brand == null ) {
            return null;
        }
        Long id = brand.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String productBrandName(Product product) {
        if ( product == null ) {
            return null;
        }
        Brand brand = product.getBrand();
        if ( brand == null ) {
            return null;
        }
        String name = brand.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}

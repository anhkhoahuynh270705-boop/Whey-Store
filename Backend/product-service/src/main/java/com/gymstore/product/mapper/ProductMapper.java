package com.gymstore.product.mapper;

import com.gymstore.product.dto.response.ProductImageDto;
import com.gymstore.product.dto.response.ProductListItemDto;
import com.gymstore.product.dto.response.ProductDto;
import com.gymstore.product.dto.request.ProductRequest;
import com.gymstore.product.entity.Product;
import com.gymstore.product.entity.ProductImage;
import com.gymstore.product.enums.ProductStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * MapStruct mapper giữa Product entity và các DTO.
 * category/brand/images được thiết lập thủ công trong service (tra cứu theo id).
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductImageDto toImageDto(ProductImage image);

    List<ProductImageDto> toImageDtos(List<ProductImage> images);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductImage toImageEntity(ProductImageDto dto);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "status", source = "status", qualifiedByName = "enumToString")
    ProductDto toDto(Product product);

    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "primaryImage", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "enumToString")
    ProductListItemDto toListDto(Product product);

    /** Tạo ProductListItemDto kèm ảnh đại diện. */
    default ProductListItemDto toListDto(Product product, String primaryImage) {
        ProductListItemDto dto = toListDto(product);
        return new ProductListItemDto(
                dto.id(), dto.name(), dto.slug(), dto.price(), dto.salePrice(),
                dto.brandName(), dto.categoryName(), primaryImage, dto.status());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToEnum")
    Product toEntity(ProductRequest request);

    @Named("enumToString")
    default String enumToString(Enum<?> e) {
        return e == null ? null : e.name();
    }

    @Named("stringToEnum")
    default ProductStatus stringToEnum(String s) {
        return s == null ? null : ProductStatus.valueOf(s);
    }
}

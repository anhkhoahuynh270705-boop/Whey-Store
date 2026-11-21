package com.gymstore.cart.mapper;

import com.gymstore.cart.dto.response.CartDto;
import com.gymstore.cart.dto.response.CartItem;
import com.gymstore.cart.dto.response.ProductSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    /**
     * Map ProductSummary and quantity to CartItem.
     */
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "imageUrl", expression = "java(product.primaryImage())")
    @Mapping(target = "quantity", source = "quantity")
    CartItem toCartItem(ProductSummary product, int quantity);

    /**
     * Create CartItem with updated quantity.
     */
    @Mapping(target = "productId", source = "item.productId")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "price", source = "item.price")
    @Mapping(target = "salePrice", source = "item.salePrice")
    @Mapping(target = "imageUrl", source = "item.imageUrl")
    @Mapping(target = "quantity", source = "quantity")
    CartItem withQuantity(CartItem item, int quantity);

    /**
     * Merge incoming cart item into existing item.
     */
    @Mapping(target = "productId", source = "incoming.productId")
    @Mapping(target = "name", source = "incoming.name")
    @Mapping(target = "price", source = "incoming.price")
    @Mapping(target = "salePrice", source = "incoming.salePrice")
    @Mapping(target = "imageUrl", source = "incoming.imageUrl")
    @Mapping(target = "quantity", expression = "java((existing != null ? existing.quantity() : 0) + incoming.quantity())")
    CartItem mergeItem(CartItem existing, CartItem incoming);

    /** Build CartDto response from attributes calculated by CartService. */
    CartDto toCartDto(String cartOwner, List<CartItem> items, BigDecimal totalAmount, int totalQuantity);
}

package com.gymstore.cart.mapper;

import com.gymstore.cart.dto.response.CartDto;
import com.gymstore.cart.dto.response.CartItem;
import com.gymstore.cart.dto.response.ProductSummary;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-20T11:30:27+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartItem toCartItem(ProductSummary product, int quantity) {
        if ( product == null ) {
            return null;
        }

        Long productId = null;
        String name = null;
        BigDecimal price = null;
        BigDecimal salePrice = null;
        if ( product != null ) {
            productId = product.id();
            name = product.name();
            price = product.price();
            salePrice = product.salePrice();
        }
        int quantity1 = 0;
        quantity1 = quantity;

        String imageUrl = product.primaryImage();

        CartItem cartItem = new CartItem( productId, name, price, salePrice, quantity1, imageUrl );

        return cartItem;
    }

    @Override
    public CartItem withQuantity(CartItem item, int quantity) {
        if ( item == null ) {
            return null;
        }

        Long productId = null;
        String name = null;
        BigDecimal price = null;
        BigDecimal salePrice = null;
        String imageUrl = null;
        if ( item != null ) {
            productId = item.productId();
            name = item.name();
            price = item.price();
            salePrice = item.salePrice();
            imageUrl = item.imageUrl();
        }
        int quantity1 = 0;
        quantity1 = quantity;

        CartItem cartItem = new CartItem( productId, name, price, salePrice, quantity1, imageUrl );

        return cartItem;
    }

    @Override
    public CartItem mergeItem(CartItem existing, CartItem incoming) {
        if ( existing == null && incoming == null ) {
            return null;
        }

        Long productId = null;
        String name = null;
        BigDecimal price = null;
        BigDecimal salePrice = null;
        String imageUrl = null;
        if ( incoming != null ) {
            productId = incoming.productId();
            name = incoming.name();
            price = incoming.price();
            salePrice = incoming.salePrice();
            imageUrl = incoming.imageUrl();
        }

        int quantity = (existing != null ? existing.quantity() : 0) + incoming.quantity();

        CartItem cartItem = new CartItem( productId, name, price, salePrice, quantity, imageUrl );

        return cartItem;
    }

    @Override
    public CartDto toCartDto(String cartOwner, List<CartItem> items, BigDecimal totalAmount, int totalQuantity) {
        if ( cartOwner == null && items == null && totalAmount == null ) {
            return null;
        }

        String cartOwner1 = null;
        cartOwner1 = cartOwner;
        List<CartItem> items1 = null;
        List<CartItem> list = items;
        if ( list != null ) {
            items1 = new ArrayList<CartItem>( list );
        }
        BigDecimal totalAmount1 = null;
        totalAmount1 = totalAmount;
        int totalQuantity1 = 0;
        totalQuantity1 = totalQuantity;

        CartDto cartDto = new CartDto( cartOwner1, items1, totalAmount1, totalQuantity1 );

        return cartDto;
    }
}

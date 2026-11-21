package com.gymstore.cart.helper;

import com.gymstore.cart.dto.response.CartItem;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.List;

public final class CartHelper {

    private CartHelper() {}

    public static BigDecimal calculateTotalAmount(List<CartItem> items) {
        return items.stream()
                .map(item -> {
                    BigDecimal unitPrice = item.salePrice() != null ? item.salePrice() : item.price();
                    return unitPrice.multiply(BigDecimal.valueOf(item.quantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static int calculateTotalQuantity(List<CartItem> items) {
        return items.stream().mapToInt(CartItem::quantity).sum();
    }

    public static CartItem readItem(RedisTemplate<String, Object> redis, String cartKey, Long productId) {
        Object value = redis.opsForHash().get(cartKey, String.valueOf(productId));
        return value instanceof CartItem item ? item : null;
    }

    public static String extractOwner(String cartKey) {
        if (cartKey == null) return "guest:unknown";
        if (cartKey.startsWith("cart:user:")) {
            return cartKey.substring("cart:user:".length());
        }
        if (cartKey.startsWith("cart:guest:")) {
            return "guest:" + cartKey.substring("cart:guest:".length());
        }
        return cartKey;
    }
}

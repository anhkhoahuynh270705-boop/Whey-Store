package com.gymstore.cart.service.impl;

import com.gymstore.cart.client.ProductClient;
import com.gymstore.cart.dto.response.CartDto;
import com.gymstore.cart.dto.response.CartItem;
import com.gymstore.cart.dto.request.CartItemRequest;
import com.gymstore.cart.dto.response.ProductSummary;
import com.gymstore.cart.dto.response.CartIdentity;
import com.gymstore.cart.helper.CartHelper;
import com.gymstore.cart.mapper.CartMapper;
import com.gymstore.cart.service.ICartService;
import com.gymstore.common.security.JwtUser;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AccessLevel;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements ICartService {

    private static final Duration TTL = Duration.ofDays(7);

    RedisTemplate<String, Object> redis;
    ProductClient productClient;
    CartMapper cartMapper;

    @Override
    public CartIdentity resolveIdentity(String guestIdHeader) {
        String userId = JwtUser.userId();
        if (userId != null && !userId.isBlank()) {
            return new CartIdentity("cart:user:" + userId, userId, false);
        }
        if (guestIdHeader != null && !guestIdHeader.isBlank()) {
            return new CartIdentity("cart:guest:" + guestIdHeader, guestIdHeader, false);
        }
        String newId = UUID.randomUUID().toString();
        return new CartIdentity("cart:guest:" + newId, newId, true);
    }

    @Override
    public CartDto getCart(String cartKey) {
        Map<Object, Object> raw = redis.<Object, Object>opsForHash().entries(cartKey);
        List<CartItem> items = new ArrayList<>();
        for (Object v : raw.values()) {
            if (v instanceof CartItem ci) {
                items.add(ci);
            }
        }
        items.sort(Comparator.comparing(CartItem::productId));
        return cartMapper.toCartDto(
                CartHelper.extractOwner(cartKey),
                items,
                CartHelper.calculateTotalAmount(items),
                CartHelper.calculateTotalQuantity(items));
    }

    @Override
    public CartDto addItem(String cartKey, CartItemRequest request) {
        ProductSummary product = productClient.getProduct(request.productId());
        int qty = request.quantity();

        CartItem existing = CartHelper.readItem(redis, cartKey, request.productId());
        if (existing != null) {
            qty += existing.quantity();
        }

        CartItem item = cartMapper.toCartItem(product, qty);
        redis.opsForHash().put(cartKey, String.valueOf(request.productId()), item);
        redis.expire(cartKey, TTL);
        return getCart(cartKey);
    }

    @Override
    public CartDto updateItem(String cartKey, Long productId, int quantity) {
        if (quantity <= 0) {
            return removeItem(cartKey, productId);
        }
        CartItem existing = CartHelper.readItem(redis, cartKey, productId);
        if (existing == null) {
            return addItem(cartKey, new CartItemRequest(productId, quantity));
        }
        CartItem updated = cartMapper.withQuantity(existing, quantity);
        redis.opsForHash().put(cartKey, String.valueOf(productId), updated);
        redis.expire(cartKey, TTL);
        return getCart(cartKey);
    }

    @Override
    public CartDto removeItem(String cartKey, Long productId) {
        redis.opsForHash().delete(cartKey, String.valueOf(productId));
        return getCart(cartKey);
    }

    @Override
    public void clear(String cartKey) {
        redis.delete(cartKey);
    }

    @Override
    public CartDto merge(String userId, String guestId) {
        String userKey = "cart:user:" + userId;
        String guestKey = "cart:guest:" + guestId;
        Map<Object, Object> guestItems = redis.<Object, Object>opsForHash().entries(guestKey);
        for (Object v : guestItems.values()) {
            if (v instanceof CartItem guestItem) {
                CartItem existing = CartHelper.readItem(redis, userKey, guestItem.productId());
                CartItem merged = cartMapper.mergeItem(existing, guestItem);
                redis.opsForHash().put(userKey, String.valueOf(guestItem.productId()), merged);
            }
        }
        redis.expire(userKey, TTL);
        redis.delete(guestKey);
        return getCart(userKey);
    }

}

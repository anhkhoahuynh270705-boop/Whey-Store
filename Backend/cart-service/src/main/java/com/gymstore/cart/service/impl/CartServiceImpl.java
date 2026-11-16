package com.gymstore.cart.service.impl;

import com.gymstore.cart.client.ProductClient;
import com.gymstore.cart.dto.response.CartDto;
import com.gymstore.cart.dto.response.CartItem;
import com.gymstore.cart.dto.request.CartItemRequest;
import com.gymstore.cart.dto.response.ProductSummary;
import com.gymstore.cart.dto.response.CartIdentity;
import com.gymstore.cart.mapper.CartMapper;
import com.gymstore.cart.service.ICartService;
import com.gymstore.common.security.JwtUser;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of cart service.
 * Cart stored in Redis Hash by owner (user or guest), TTL 7 days.
 * Key: cart:user:{userId} or cart:guest:{guestId}. Field = productId, value = CartItem (JSON).
 */
@Service
public class CartServiceImpl implements ICartService {

    private static final Duration TTL = Duration.ofDays(7);

    private final RedisTemplate<String, Object> redis;
    private final ProductClient productClient;
    private final CartMapper cartMapper;

    public CartServiceImpl(RedisTemplate<String, Object> redis,
                           ProductClient productClient,
                           CartMapper cartMapper) {
        this.redis = redis;
        this.productClient = productClient;
        this.cartMapper = cartMapper;
    }

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
        return buildCartDto(cartKey, items);
    }

    @Override
    public CartDto addItem(String cartKey, CartItemRequest request) {
        ProductSummary product = productClient.getProduct(request.productId());
        int qty = request.quantity();

        CartItem existing = readItem(cartKey, request.productId());
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
        CartItem existing = readItem(cartKey, productId);
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
                CartItem existing = readItem(userKey, guestItem.productId());
                CartItem merged = cartMapper.mergeItem(existing, guestItem);
                redis.opsForHash().put(userKey, String.valueOf(guestItem.productId()), merged);
            }
        }
        redis.expire(userKey, TTL);
        redis.delete(guestKey);
        return getCart(userKey);
    }

    // Helper methods

    private CartItem readItem(String cartKey, Long productId) {
        Object v = redis.opsForHash().get(cartKey, String.valueOf(productId));
        return v instanceof CartItem ci ? ci : null;
    }

    private CartDto buildCartDto(String cartKey, List<CartItem> items) {
        BigDecimal total = items.stream()
                .map(i -> effectivePrice(i).multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalQty = items.stream().mapToInt(CartItem::quantity).sum();
        String owner = extractOwner(cartKey);
        return cartMapper.toCartDto(owner, items, total, totalQty);
    }

    private BigDecimal effectivePrice(CartItem item) {
        return item.salePrice() != null ? item.salePrice() : item.price();
    }

    private String extractOwner(String cartKey) {
        if (cartKey.startsWith("cart:user:")) {
            return cartKey.substring("cart:user:".length());
        }
        return "guest:" + cartKey.substring("cart:guest:".length());
    }
}

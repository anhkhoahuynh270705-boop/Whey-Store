package com.gymstore.cart.service;

import com.gymstore.cart.dto.response.CartDto;
import com.gymstore.cart.dto.response.CartIdentity;
import com.gymstore.cart.dto.request.CartItemRequest;

/**
 * Interface for cart service operations.
 * Manages shopping cart stored in Redis with 7-day TTL.
 */
public interface ICartService {

    /**
     * Resolve cart identity based on JWT user or guest ID.
     * Priority: authenticated user > guest ID > generate new guest ID.
     */
    CartIdentity resolveIdentity(String guestIdHeader);

    /**
     * Get current cart contents.
     */
    CartDto getCart(String cartKey);

    /**
     * Add item to cart. Fetches product price from product-service.
     */
    CartDto addItem(String cartKey, CartItemRequest request);

    /**
     * Update item quantity. If quantity <= 0, removes the item.
     */
    CartDto updateItem(String cartKey, Long productId, int quantity);

    /**
     * Remove item from cart.
     */
    CartDto removeItem(String cartKey, Long productId);

    /**
     * Clear all items from cart.
     */
    void clear(String cartKey);

    /**
     * Merge guest cart into user cart after login, then delete guest cart.
     */
    CartDto merge(String userId, String guestId);
}

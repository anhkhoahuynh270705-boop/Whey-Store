package com.gymstore.cart.service;

import com.gymstore.cart.dto.response.CartDto;
import com.gymstore.cart.dto.response.CartIdentity;
import com.gymstore.cart.dto.request.CartItemRequest;

public interface ICartService {

    /**
     * Resolve cart identity based on JWT user or guest ID.
     * Priority: authenticated user > guest ID > generate new guest ID.
     */
    CartIdentity resolveIdentity(String guestIdHeader);

    CartDto getCart(String cartKey);

    CartDto addItem(String cartKey, CartItemRequest request);

    CartDto updateItem(String cartKey, Long productId, int quantity);

    CartDto removeItem(String cartKey, Long productId);

    void clear(String cartKey);

    CartDto merge(String userId, String guestId);
}

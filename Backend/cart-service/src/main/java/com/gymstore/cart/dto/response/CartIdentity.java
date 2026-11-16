package com.gymstore.cart.dto.response;

public record CartIdentity(
    String cartKey, 
    String ownerId, 
    boolean generatedGuestId) {
}

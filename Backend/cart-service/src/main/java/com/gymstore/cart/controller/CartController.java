package com.gymstore.cart.controller;

import com.gymstore.cart.dto.response.CartDto;
import com.gymstore.cart.dto.request.CartItemRequest;
import com.gymstore.cart.service.ICartService;
import com.gymstore.common.security.JwtUser;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.gymstore.cart.dto.response.CartIdentity;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartDto getCart(@RequestHeader(value = "X-Cart-Id", required = false) String guestId,
                           HttpServletResponse response) {
        CartIdentity id = cartService.resolveIdentity(guestId);
        if (id.generatedGuestId()) {
            response.setHeader("X-Cart-Id", id.ownerId());
        }
        return cartService.getCart(id.cartKey());
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public CartDto addItem(@RequestHeader(value = "X-Cart-Id", required = false) String guestId,
                           @Valid @RequestBody CartItemRequest request,
                           HttpServletResponse response) {
        CartIdentity id = cartService.resolveIdentity(guestId);
        if (id.generatedGuestId()) {
            response.setHeader("X-Cart-Id", id.ownerId());
        }
        return cartService.addItem(id.cartKey(), request);
    }

    @PutMapping("/items/{productId}")
    public CartDto updateItem(@RequestHeader(value = "X-Cart-Id", required = false) String guestId,
                              @PathVariable Long productId,
                              @RequestParam @jakarta.validation.constraints.Min(1) int quantity,
                              HttpServletResponse response) {
        CartIdentity id = cartService.resolveIdentity(guestId);
        if (id.generatedGuestId()) {
            response.setHeader("X-Cart-Id", id.ownerId());
        }
        return cartService.updateItem(id.cartKey(), productId, quantity);
    }

    @DeleteMapping("/items/{productId}")
    public CartDto removeItem(@RequestHeader(value = "X-Cart-Id", required = false) String guestId,
                               @PathVariable Long productId,
                               HttpServletResponse response) {
        CartIdentity id = cartService.resolveIdentity(guestId);
        if (id.generatedGuestId()) {
            response.setHeader("X-Cart-Id", id.ownerId());
        }
        return cartService.removeItem(id.cartKey(), productId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@RequestHeader(value = "X-Cart-Id", required = false) String guestId) {
        CartIdentity id = cartService.resolveIdentity(guestId);
        cartService.clear(id.cartKey());
    }

    /** Gộp giỏ guest vào giỏ user đang đăng nhập. Yêu cầu JWT. */
    @PostMapping("/merge")
    public CartDto mergeCart(@RequestHeader(value = "X-Cart-Id", required = false) String guestId) {
        String userId = JwtUser.userId();
        if (userId == null) {
            throw new IllegalStateException("Phải đăng nhập để gộp giỏ");
        }
        if (guestId == null || guestId.isBlank()) {
            throw new IllegalArgumentException("Thiếu X-Cart-Id của giỏ guest");
        }
        return cartService.merge(userId, guestId);
    }
}

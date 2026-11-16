package com.gymstore.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

/**
 * Tiện ích lấy thông tin user hiện tại từ SecurityContext (JWT).
 * Dùng ở cart/order/notification để biết ai đang gọi.
 */
public final class JwtUser {

    private JwtUser() {}

    /** Lấy Jwt hiện tại nếu đã xác thực. */
    public static Optional<Jwt> current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return Optional.of(jwt);
        }
        return Optional.empty();
    }

    /** subject (user id trong Keycloak). */
    public static String userId() {
        return current().map(j -> j.getSubject()).orElse(null);
    }

    /** preferred_username. */
    public static String username() {
        return current().map(j -> j.getClaimAsString("preferred_username")).orElse(null);
    }

    /** email. */
    public static String email() {
        return current().map(j -> j.getClaimAsString("email")).orElse(null);
    }

    /** fullName (kết hợp given + family). */
    public static String fullName() {
        return current().map(j -> {
            String given = j.getClaimAsString("given_name");
            String family = j.getClaimAsString("family_name");
            if (given == null) given = "";
            if (family == null) family = "";
            String name = (given + " " + family).trim();
            return name.isBlank() ? username() : name;
        }).orElse(null);
    }
}

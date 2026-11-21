package com.gymstore.gateway.service;

import com.gymstore.gateway.enums.UserRole;

public interface KeycloakUserService {

    String createUser(String username, String email, String fullName, String phone,
                      String password, UserRole role);

    void sendVerificationEmail(String adminToken, String userId, String email);

    void setUserEnabled(String userId, boolean enabled);

    void revokeSession(String refreshToken);

    boolean isAvailable();
}

package com.gymstore.gateway.dto.response;

import java.time.LocalDateTime;

/** Row values extracted from app_users before mapping to UserAccountDto. */
public record UserAccountRow(
        String id,
        String username,
        String email,
        String fullName,
        String phone,
        String role,
        String status,
        LocalDateTime createdAt
) {}

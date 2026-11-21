package com.gymstore.gateway.dto.internal;

import com.gymstore.gateway.enums.UserRole;

/** Internal data carrier — dùng để truyền dữ liệu giữa service và mapper, không expose ra ngoài. */
public record UserAccountData(
        String userId,
        String username,
        String email,
        String fullName,
        String phone,
        UserRole role,
        String status
) {}

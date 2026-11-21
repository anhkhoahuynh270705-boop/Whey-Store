package com.gymstore.gateway.dto.response;

public record DemoAccountDto(
        String role,
        String username,
        String password,
        String label,
        String icon,
        String badgeClass
) {}

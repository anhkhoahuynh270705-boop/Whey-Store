package com.gymstore.gateway.mapper;

import com.gymstore.gateway.enums.UserRole;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class UserMapperSupport {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private UserMapperSupport() {}

    public static String formatNow() {
        return LocalDateTime.now().format(FORMATTER);
    }

    @Named("toUserRole")
    public static UserRole toUserRole(String role) {
        return UserRole.fromString(role);
    }

    @Named("formatCreatedAt")
    public static String formatCreatedAt(LocalDateTime createdAt) {
        return createdAt != null ? createdAt.format(FORMATTER) : "";
    }
}

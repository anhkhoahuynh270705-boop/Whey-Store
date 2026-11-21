package com.gymstore.gateway.enums;

public enum UserRole {

    ADMIN,
    STAFF,
    CUSTOMER;

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) return false;
        for (UserRole r : values()) {
            if (r.name().equalsIgnoreCase(value.trim())) return true;
        }
        return false;
    }

    public static UserRole fromString(String value) {
        if (value == null || value.isBlank()) return CUSTOMER;
        for (UserRole r : values()) {
            if (r.name().equalsIgnoreCase(value.trim())) return r;
        }
        return CUSTOMER;
    }
}

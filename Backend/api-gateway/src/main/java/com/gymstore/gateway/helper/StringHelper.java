package com.gymstore.gateway.helper;

public final class StringHelper {

    private StringHelper() {}

    /** Trim chuỗi, trả về "" nếu null. */
    public static String normalize(String s) {
        return s != null ? s.trim() : "";
    }

    public static String[] splitFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) return new String[]{"", ""};
        String trimmed   = fullName.trim();
        int    lastSpace = trimmed.lastIndexOf(' ');
        return lastSpace > 0
                ? new String[]{trimmed.substring(0, lastSpace), trimmed.substring(lastSpace + 1)}
                : new String[]{trimmed, ""};
    }
}

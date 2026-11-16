package com.gymstore.common.dto.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> violations
        ) {

    public record FieldViolation(String field, String message) {}

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, List.of());
    }

    public static ErrorResponse of(int status, String error, String message, String path, List<FieldViolation> violations) {
        return new ErrorResponse(Instant.now(), status, error, message, path, violations);
    }
}

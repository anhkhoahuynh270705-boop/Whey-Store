package com.gymstore.common.exception;

/** Tài nguyên không tồn tại → HTTP 404. */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Object id) {
        super(404, resource + " không tồn tại: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(404, message);
    }
}

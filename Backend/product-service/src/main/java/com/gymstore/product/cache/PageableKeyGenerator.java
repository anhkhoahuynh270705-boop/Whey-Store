package com.gymstore.product.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Sinh key cache cho các method nhận ProductFilter + Pageable (toString đã có đủ thông tin).
 * Đảm bảo key phân biệt theo bộ lọc + trang.
 */
@Component("pageableKeyGenerator")
public class PageableKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb = new StringBuilder();
        for (Object p : params) {
            sb.append(p == null ? "null" : p.toString()).append('|');
        }
        return sb.toString();
    }
}

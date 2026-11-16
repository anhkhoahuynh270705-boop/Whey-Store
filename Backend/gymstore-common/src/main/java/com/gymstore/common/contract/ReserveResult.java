package com.gymstore.common.contract;

import java.util.List;

/**
 * Kết quả giữ hàng: đủ=true, kèm danh sách item không đủ khi false.
 */
public record ReserveResult(
        boolean success,
        String reason,
        List<ReserveItem> insufficientItems
        ) {}

package com.gymstore.common.contract;

import java.util.List;

/** Yêu cầu giữ hàng (reserve) cho một đơn. */
public record ReserveRequest(String orderId, List<ReserveItem> items) {}

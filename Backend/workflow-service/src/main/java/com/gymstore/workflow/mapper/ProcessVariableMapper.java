package com.gymstore.workflow.mapper;

import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.common.dto.order.request.CreateOrderRequest;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MapStruct mapper chuyển đổi Camunda process variables ↔ DTO/typed objects.
 *
 * <p>workflow-service không có entity riêng — tất cả state được lưu trong Camunda
 * process variables (dạng Map&lt;String, Object&gt;). Mapper này chuẩn hóa việc
 * đọc/ghi variables và build DTO để gọi các service khác qua Feign.
 */
@Mapper(componentModel = "spring")
public interface ProcessVariableMapper {

    // ── Variable key constants ───────────────────────────────────────────────

    String KEY_USER_ID         = "userId";
    String KEY_CART_ID         = "cartId";
    String KEY_ORDER_ID        = "orderId";
    String KEY_ORDER_NUMBER    = "orderNumber";
    String KEY_PROCESS_ID      = "processInstanceId";
    String KEY_PAYMENT_METHOD  = "paymentMethod";
    String KEY_TOTAL_AMOUNT    = "totalAmount";
    String KEY_ITEMS           = "items";
    String KEY_SHIPPING        = "shippingAddress";
    String KEY_EMAIL           = "email";
    String KEY_FULL_NAME       = "fullName";
    String KEY_CANCEL_REASON   = "cancelReason";

    // ── Builders ─────────────────────────────────────────────────────────────

    /**
     * Build process variables map từ checkout request + userId.
     * Dùng khi khởi động process instance từ {@code WorkflowController}.
     */
    default Map<String, Object> toStartVariables(String userId, String cartId,
                                                 String paymentMethod, String shipping,
                                                 List<CheckoutItem> items) {
        Map<String, Object> vars = new HashMap<>();
        vars.put(KEY_USER_ID, userId);
        vars.put(KEY_CART_ID, cartId);
        vars.put(KEY_PAYMENT_METHOD, paymentMethod);
        vars.put(KEY_SHIPPING, shipping);
        vars.put(KEY_ITEMS, items);
        return vars;
    }

    /**
     * Build {@link CreateOrderRequest} từ process variables.
     * Dùng trong {@code CreateOrderDelegate} khi gọi order-service.
     */
    @SuppressWarnings("unchecked")
    default CreateOrderRequest toCreateOrderRequest(Map<String, Object> vars) {
        String userId        = (String) vars.get(KEY_USER_ID);
        String userEmail     = (String) vars.get(KEY_EMAIL);
        String fullName      = (String) vars.get(KEY_FULL_NAME);
        String shippingStr   = (String) vars.get(KEY_SHIPPING);
        String paymentMethod = (String) vars.get(KEY_PAYMENT_METHOD);
        List<CheckoutItem> items = (List<CheckoutItem>) vars.get(KEY_ITEMS);
        Object totalAmtObj   = vars.get(KEY_TOTAL_AMOUNT);
        BigDecimal totalAmount = totalAmtObj instanceof BigDecimal bd ? bd
                : totalAmtObj != null ? new BigDecimal(String.valueOf(totalAmtObj)) : null;

        return new CreateOrderRequest(userId, userEmail, fullName, shippingStr, paymentMethod,
                items, totalAmount);
    }

    /**
     * Lấy giá trị String an toàn từ variables.
     */
    default String getString(Map<String, Object> vars, String key) {
        Object val = vars.get(key);
        return val != null ? String.valueOf(val) : null;
    }

    /**
     * Lấy giá trị Long an toàn từ variables.
     */
    default Long getLong(Map<String, Object> vars, String key) {
        Object val = vars.get(key);
        if (val == null) return null;
        if (val instanceof Long l) return l;
        if (val instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(val)); } catch (NumberFormatException e) { return null; }
    }
}

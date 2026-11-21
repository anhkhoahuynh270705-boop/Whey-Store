package com.gymstore.workflow.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymstore.common.dto.order.request.CheckoutItem;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("validateOrderDelegate")
public class ValidateOrderDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ValidateOrderDelegate.class);

    private final ObjectMapper objectMapper;

    public ValidateOrderDelegate(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String orderId = (String) execution.getVariable("orderId");
        List<CheckoutItem> items = resolveItems(execution);
        log.info("[ValidateOrder] processId={} orderId={}", execution.getProcessInstanceId(), orderId);

        if (items == null || items.isEmpty()) {
            log.warn("[ValidateOrder] items rỗng");
            throw new BpmnError("VALIDATION_FAILED", "Đơn hàng không có sản phẩm nào");
        }

        for (CheckoutItem item : items) {
            if (item.unitPrice() == null || item.unitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BpmnError("VALIDATION_FAILED", "Giá sản phẩm không hợp lệ: " + item.productName());
            }
            if (item.quantity() <= 0) {
                throw new BpmnError("VALIDATION_FAILED", "Số lượng không hợp lệ: " + item.productName());
            }
        }

        BigDecimal total = items.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lưu lại items đã typed để các delegate sau dùng được
        execution.setVariable("items", items);
        execution.setVariable("totalAmount", total);
        log.info("[ValidateOrder] Hợp lệ. totalAmount={}", total);
    }

    /**
     * Camunda lưu process variables vào DB dạng JSON, khi đọc lại Jackson
     * deserialize List<CheckoutItem> thành List<LinkedHashMap>. Method này
     * xử lý cả 2 trường hợp: đã là CheckoutItem hoặc còn là LinkedHashMap.
     */
    @SuppressWarnings("unchecked")
    private List<CheckoutItem> resolveItems(DelegateExecution execution) {
        Object raw = execution.getVariable("items");
        if (raw == null) return null;

        if (raw instanceof List<?> list) {
            if (list.isEmpty()) return List.of();

            Object first = list.get(0);
            if (first instanceof CheckoutItem) {
                // Đã đúng kiểu — không cần convert
                return (List<CheckoutItem>) list;
            }
            if (first instanceof Map) {
                // Camunda đọc từ DB → LinkedHashMap, convert lại
                return list.stream()
                        .map(item -> objectMapper.convertValue(item, CheckoutItem.class))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }
}

package com.gymstore.workflow.delegate;

import com.gymstore.common.dto.order.request.CheckoutItem;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Task: Validate đơn hàng.
 * Kiểm tra items không rỗng, giá > 0. Trong production, gọi product-service
 * để xác nhận sản phẩm còn bán + giá chưa thay đổi.
 * Ném BpmnError("VALIDATION_FAILED") nếu không hợp lệ → Error Boundary sẽ bắt.
 */
@Component("validateOrderDelegate")
public class ValidateOrderDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ValidateOrderDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        String orderId = (String) execution.getVariable("orderId");
        @SuppressWarnings("unchecked")
        List<CheckoutItem> items = (List<CheckoutItem>) execution.getVariable("items");
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
        // Tính tổng tiền và lưu vào process variable để dùng ở các task sau
        BigDecimal total = items.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        execution.setVariable("totalAmount", total);
        log.info("[ValidateOrder] Hợp lệ. totalAmount={}", total);
    }
}

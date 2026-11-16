package com.gymstore.workflow.delegate;

import com.gymstore.workflow.client.OrderClient;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Task: Hủy đơn khi hết hàng (chưa tạo đơn thật trong order-service, chỉ ghi log và cập nhật variable).
 * Nếu orderId chưa có (đơn chưa được tạo do hết hàng trước khi tạo) thì chỉ log.
 */
@Component("cancelOrderNoStockDelegate")
public class CancelOrderNoStockDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CancelOrderNoStockDelegate.class);
    private final OrderClient orderClient;

    public CancelOrderNoStockDelegate(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Long orderId = (Long) execution.getVariable("orderId");
        String reason = "Hết hàng: " + execution.getVariable("stockReservedReason");
        execution.setVariable("cancelReason", reason);
        log.info("[CancelOrderNoStock] processId={} reason={}", execution.getProcessInstanceId(), reason);
        if (orderId != null) {
            orderClient.updateStatus(orderId, "CANCELLED", reason);
        }
    }
}

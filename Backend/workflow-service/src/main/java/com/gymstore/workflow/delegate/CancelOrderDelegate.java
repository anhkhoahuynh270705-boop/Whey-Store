package com.gymstore.workflow.delegate;

import com.gymstore.workflow.client.OrderClient;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("cancelOrderDelegate")
public class CancelOrderDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CancelOrderDelegate.class);
    private final OrderClient orderClient;

    public CancelOrderDelegate(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Long orderId = (Long) execution.getVariable("orderId");
        String reason = (String) execution.getVariable("cancelReason");
        if (orderId == null) {
            log.warn("[CancelOrder] Không có orderId, bỏ qua");
            return;
        }
        log.info("[CancelOrder] orderId={} reason={}", orderId, reason);
        orderClient.updateStatus(orderId, "CANCELLED", reason != null ? reason : "Hủy trong quy trình");
    }
}

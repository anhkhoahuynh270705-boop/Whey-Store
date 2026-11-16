package com.gymstore.workflow.delegate;

import com.gymstore.workflow.client.OrderClient;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Task: Cập nhật đơn thành COMPLETED sau thanh toán thành công. */
@Component("updateOrderCompletedDelegate")
public class UpdateOrderCompletedDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(UpdateOrderCompletedDelegate.class);
    private final OrderClient orderClient;

    public UpdateOrderCompletedDelegate(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Long orderId = (Long) execution.getVariable("orderId");
        log.info("[UpdateOrderCompleted] orderId={}", orderId);
        orderClient.updateStatus(orderId, "COMPLETED", "Thanh toán và xử lý đơn thành công");
    }
}

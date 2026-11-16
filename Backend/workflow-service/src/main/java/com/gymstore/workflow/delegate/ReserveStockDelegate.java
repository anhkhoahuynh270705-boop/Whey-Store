package com.gymstore.workflow.delegate;

import com.gymstore.common.contract.ReserveItem;
import com.gymstore.common.contract.ReserveRequest;
import com.gymstore.common.contract.ReserveResult;
import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.workflow.client.InventoryClient;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Task: Giữ hàng (reserve stock). Gọi inventory-service /internal/inventory/reserve.
 * Lưu kết quả vào process variable "stockReserved" để gateway đọc.
 */
@Component("reserveStockDelegate")
public class ReserveStockDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ReserveStockDelegate.class);

    private final InventoryClient inventoryClient;

    public ReserveStockDelegate(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) {
        String processId = execution.getProcessInstanceId();
        List<CheckoutItem> items = (List<CheckoutItem>) execution.getVariable("items");
        log.info("[ReserveStock] processId={}", processId);

        List<ReserveItem> reserveItems = items.stream()
                .map(i -> new ReserveItem(i.productId(), i.quantity()))
                .toList();

        ReserveResult result = inventoryClient.reserve(new ReserveRequest(processId, reserveItems));
        execution.setVariable("stockReserved", result.success());
        execution.setVariable("stockReservedReason", result.reason());
        log.info("[ReserveStock] success={} reason={}", result.success(), result.reason());
    }
}

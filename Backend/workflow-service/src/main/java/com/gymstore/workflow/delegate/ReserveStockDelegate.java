package com.gymstore.workflow.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymstore.common.contract.ReserveItem;
import com.gymstore.common.contract.ReserveRequest;
import com.gymstore.common.contract.ReserveResult;
import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.workflow.client.InventoryClient;
import com.gymstore.workflow.mapper.ProcessVariableMapper;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("reserveStockDelegate")
public class ReserveStockDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ReserveStockDelegate.class);

    private final InventoryClient inventoryClient;
    private final ProcessVariableMapper processVariableMapper;
    private final ObjectMapper objectMapper;

    public ReserveStockDelegate(InventoryClient inventoryClient,
                                ProcessVariableMapper processVariableMapper,
                                ObjectMapper objectMapper) {
        this.inventoryClient = inventoryClient;
        this.processVariableMapper = processVariableMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String processId = execution.getProcessInstanceId();
        List<CheckoutItem> items = processVariableMapper.resolveItems(execution, objectMapper);
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

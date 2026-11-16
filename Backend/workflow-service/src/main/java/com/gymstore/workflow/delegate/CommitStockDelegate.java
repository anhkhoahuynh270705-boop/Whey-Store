package com.gymstore.workflow.delegate;

import com.gymstore.workflow.client.InventoryClient;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Task: Trừ kho chính thức (commit). orderId dùng là processInstanceId khi reserve. */
@Component("commitStockDelegate")
public class CommitStockDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CommitStockDelegate.class);
    private final InventoryClient inventoryClient;

    public CommitStockDelegate(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String processId = execution.getProcessInstanceId();
        log.info("[CommitStock] processId={}", processId);
        inventoryClient.commit(processId);
    }
}

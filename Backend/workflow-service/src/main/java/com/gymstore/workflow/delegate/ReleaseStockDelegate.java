package com.gymstore.workflow.delegate;

import com.gymstore.workflow.client.InventoryClient;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("releaseStockDelegate")
public class ReleaseStockDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ReleaseStockDelegate.class);
    private final InventoryClient inventoryClient;

    public ReleaseStockDelegate(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String processId = execution.getProcessInstanceId();
        log.info("[ReleaseStock] processId={}", processId);
        inventoryClient.release(processId);
    }
}

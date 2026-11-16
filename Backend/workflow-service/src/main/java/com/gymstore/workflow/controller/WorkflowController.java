package com.gymstore.workflow.controller;

import com.gymstore.common.dto.order.response.CheckoutResponse;
import com.gymstore.workflow.service.IPurchaseProcessService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** REST endpoint để order-service start Camunda process. */
@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final IPurchaseProcessService purchaseProcessService;

    public WorkflowController(IPurchaseProcessService purchaseProcessService) {
        this.purchaseProcessService = purchaseProcessService;
    }

    @PostMapping("/purchase/start")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CheckoutResponse startPurchase(@RequestBody Map<String, Object> variables) {
        return purchaseProcessService.start(variables);
    }

    @GetMapping("/process/{processInstanceId}")
    public Map<String, Object> getProcessStatus(@PathVariable String processInstanceId) {
        return purchaseProcessService.getStatus(processInstanceId);
    }
}

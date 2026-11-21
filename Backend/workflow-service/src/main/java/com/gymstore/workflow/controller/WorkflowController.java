package com.gymstore.workflow.controller;

import com.gymstore.common.dto.order.response.CheckoutResponse;
import com.gymstore.workflow.dto.StartProcessRequest;
import com.gymstore.workflow.mapper.ProcessVariableMapper;
import com.gymstore.workflow.service.IPurchaseProcessService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final IPurchaseProcessService purchaseProcessService;
    private final ProcessVariableMapper processVariableMapper;

    @PostMapping("/purchase/start")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CheckoutResponse startPurchase(@RequestBody StartProcessRequest request) {
        Map<String, Object> variables = processVariableMapper.toStartVariables(
                request.userId(),
                request.cartId(),
                request.paymentMethod(),
                request.shippingAddress(),
                request.items()
        );
        variables.put(ProcessVariableMapper.KEY_EMAIL, request.email());
        variables.put(ProcessVariableMapper.KEY_FULL_NAME, request.fullName());

        return purchaseProcessService.start(variables);
    }

    @GetMapping("/process/{processInstanceId}")
    public Map<String, Object> getProcessStatus(@PathVariable String processInstanceId) {
        return purchaseProcessService.getStatus(processInstanceId);
    }
}

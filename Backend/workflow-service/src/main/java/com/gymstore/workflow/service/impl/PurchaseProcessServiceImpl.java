package com.gymstore.workflow.service.impl;

import com.gymstore.common.dto.order.response.CheckoutResponse;
import com.gymstore.workflow.service.IPurchaseProcessService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseProcessServiceImpl implements IPurchaseProcessService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseProcessServiceImpl.class);
    private static final String PROCESS_DEF_KEY = "purchase-product-process";
    RuntimeService runtimeService;

    @Override
    public CheckoutResponse start(Map<String, Object> variables) {
        log.info("Khởi động process cho userId={}", variables.get("userId"));
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                PROCESS_DEF_KEY, (String) variables.get("cartId"), variables);
        log.info("[PurchaseProcess] processInstanceId={}", instance.getProcessInstanceId());

        return new CheckoutResponse(null, null, instance.getProcessInstanceId(),
                "Đơn hàng đang được xử lý");
    }

    @Override
    public Map<String, Object> getStatus(String processInstanceId) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        Map<String, Object> result = new HashMap<>();
        result.put("processInstanceId", processInstanceId);
        if (instance == null) {
            result.put("status", "COMPLETED_OR_NOT_FOUND");
        } else {
            result.put("status", "ACTIVE");
            Object orderId = runtimeService.getVariable(processInstanceId, "orderId");
            if (orderId != null) {
                result.put("orderId", orderId);
            }
        }
        return result;
    }
}

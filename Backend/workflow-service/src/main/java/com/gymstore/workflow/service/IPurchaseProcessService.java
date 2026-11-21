package com.gymstore.workflow.service;

import com.gymstore.common.dto.order.response.CheckoutResponse;

import java.util.Map;

/**
 * Interface cho purchase process service.
 */
public interface IPurchaseProcessService {

    CheckoutResponse start(Map<String, Object> variables);

    Map<String, Object> getStatus(String processInstanceId);
}

package com.gymstore.workflow.service;

import com.gymstore.common.dto.order.response.CheckoutResponse;

import java.util.Map;

/**
 * Interface cho purchase process service.
 */
public interface IPurchaseProcessService {

    /**
     * Khởi động purchase-product-process Camunda.
     * @param variables process variables (cartId, userId, userEmail, etc.)
     * @return CheckoutResponse với processInstanceId
     */
    CheckoutResponse start(Map<String, Object> variables);

    /**
     * Lấy trạng thái process instance.
     * @param processInstanceId ID của process instance
     * @return Map chứa thông tin trạng thái process
     */
    Map<String, Object> getStatus(String processInstanceId);
}

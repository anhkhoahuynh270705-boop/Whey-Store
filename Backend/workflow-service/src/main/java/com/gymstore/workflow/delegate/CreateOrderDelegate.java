package com.gymstore.workflow.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.common.dto.order.request.CreateOrderRequest;
import com.gymstore.common.dto.order.response.OrderDto;
import com.gymstore.workflow.client.OrderClient;
import com.gymstore.workflow.mapper.ProcessVariableMapper;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Task: Tạo đơn hàng PENDING. Gọi order-service /internal/orders.
 * Lưu orderId vào process variable để các task sau dùng.
 */
@Component("createOrderDelegate")
public class CreateOrderDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CreateOrderDelegate.class);

    private final OrderClient orderClient;
    private final ProcessVariableMapper processVariableMapper;
    private final ObjectMapper objectMapper;

    public CreateOrderDelegate(OrderClient orderClient,
                               ProcessVariableMapper processVariableMapper,
                               ObjectMapper objectMapper) {
        this.orderClient = orderClient;
        this.processVariableMapper = processVariableMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String userId        = (String) execution.getVariable("userId");
        String userEmail     = (String) execution.getVariable("email");
        String userFullName  = (String) execution.getVariable("fullName");
        String shippingAddress = (String) execution.getVariable("shippingAddress");
        String paymentMethod = (String) execution.getVariable("paymentMethod");
        List<CheckoutItem> items = processVariableMapper.resolveItems(execution, objectMapper);
        BigDecimal totalAmount = (BigDecimal) execution.getVariable("totalAmount");

        log.info("[CreateOrder] userId={} paymentMethod={}", userId, paymentMethod);

        CreateOrderRequest req = new CreateOrderRequest(userId, userEmail, userFullName,
                shippingAddress, paymentMethod, items, totalAmount);
        OrderDto order = orderClient.create(req);

        execution.setVariable("orderId", order.id());
        execution.setVariable("orderNumber", order.orderNumber());
        log.info("[CreateOrder] Đã tạo đơn {} (id={})", order.orderNumber(), order.id());
    }
}

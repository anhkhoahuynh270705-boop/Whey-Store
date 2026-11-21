package com.gymstore.workflow.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.common.event.OrderCancelledEvent;
import com.gymstore.common.event.OrderItemEvent;
import com.gymstore.workflow.mapper.ProcessVariableMapper;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Task: Publish sự kiện order.cancelled lên Kafka. */
@Component("publishOrderCancelledDelegate")
public class PublishOrderCancelledDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(PublishOrderCancelledDelegate.class);
    private static final String TOPIC = "order.cancelled";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProcessVariableMapper processVariableMapper;
    private final ObjectMapper objectMapper;

    public PublishOrderCancelledDelegate(KafkaTemplate<String, Object> kafkaTemplate,
                                         ProcessVariableMapper processVariableMapper,
                                         ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.processVariableMapper = processVariableMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Long orderId        = (Long) execution.getVariable("orderId");
        String orderNumber  = (String) execution.getVariable("orderNumber");
        String userId       = (String) execution.getVariable("userId");
        String userEmail    = (String) execution.getVariable("email");
        String userFullName = (String) execution.getVariable("fullName");
        BigDecimal totalAmount = (BigDecimal) execution.getVariable("totalAmount");
        String cancelReason = (String) execution.getVariable("cancelReason");
        List<CheckoutItem> checkoutItems = processVariableMapper.resolveItems(execution, objectMapper);

        List<OrderItemEvent> itemEvents = checkoutItems == null ? List.of() :
                checkoutItems.stream()
                        .map(i -> new OrderItemEvent(i.productId(), i.productName(), i.quantity(),
                                i.unitPrice(), i.unitPrice().multiply(BigDecimal.valueOf(i.quantity()))))
                        .toList();

        OrderCancelledEvent event = new OrderCancelledEvent(
                String.valueOf(orderId), orderNumber, userId, userEmail, userFullName,
                itemEvents, totalAmount != null ? totalAmount : BigDecimal.ZERO,
                cancelReason, Instant.now());

        log.info("[PublishOrderCancelled] orderId={} reason={}", orderId, cancelReason);
        kafkaTemplate.send(TOPIC, String.valueOf(orderId), event);
    }
}

package com.gymstore.workflow.delegate;

import com.gymstore.common.event.OrderCompletedEvent;
import com.gymstore.common.event.OrderItemEvent;
import com.gymstore.common.event.ShippingAddressEvent;
import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.common.dto.order.request.ShippingAddressRequest;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Task: Publish sự kiện order.completed lên Kafka.
 * Notification-service và report-service sẽ consume.
 */
@Component("publishOrderCompletedDelegate")
public class PublishOrderCompletedDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(PublishOrderCompletedDelegate.class);
    private static final String TOPIC = "order.completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PublishOrderCompletedDelegate(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) {
        Long orderId = (Long) execution.getVariable("orderId");
        String orderNumber = (String) execution.getVariable("orderNumber");
        String userId = (String) execution.getVariable("userId");
        String userEmail = (String) execution.getVariable("userEmail");
        String userFullName = (String) execution.getVariable("userFullName");
        BigDecimal totalAmount = (BigDecimal) execution.getVariable("totalAmount");
        String paymentMethod = (String) execution.getVariable("paymentMethod");
        List<CheckoutItem> checkoutItems = (List<CheckoutItem>) execution.getVariable("items");
        ShippingAddressRequest addressRequest = (ShippingAddressRequest) execution.getVariable("shippingAddressDetail");

        List<OrderItemEvent> itemEvents = checkoutItems.stream()
                .map(i -> new OrderItemEvent(i.productId(), i.productName(), i.quantity(),
                        i.unitPrice(), i.unitPrice().multiply(BigDecimal.valueOf(i.quantity()))))
                .toList();

        ShippingAddressEvent shippingEvent = addressRequest != null
                ? new ShippingAddressEvent(addressRequest.fullName(), addressRequest.phone(),
                        addressRequest.addressLine(), addressRequest.ward(),
                        addressRequest.district(), addressRequest.city(), addressRequest.note())
                : new ShippingAddressEvent(null, null, (String) execution.getVariable("shippingAddress"),
                        null, null, null, null);

        OrderCompletedEvent event = new OrderCompletedEvent(
                String.valueOf(orderId), orderNumber, userId, userEmail, userFullName,
                itemEvents, totalAmount, shippingEvent, paymentMethod, Instant.now());

        log.info("[PublishOrderCompleted] orderId={}", orderId);
        kafkaTemplate.send(TOPIC, String.valueOf(orderId), event);
    }
}

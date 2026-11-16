package com.gymstore.order.kafka;

import com.gymstore.common.event.OrderCancelledEvent;
import com.gymstore.common.event.OrderCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer sự kiện đơn hàng → Kafka.
 * Dùng KafkaTemplate với acks=all (cấu hình ở application.yml).
 * key = orderId để đảm bảo order message trong cùng partition.
 */
@Component
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    public static final String TOPIC_COMPLETED = "order.completed";
    public static final String TOPIC_CANCELLED = "order.cancelled";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCompleted(OrderCompletedEvent event) {
        log.info("Publish order.completed: {}", event.orderId());
        kafkaTemplate.send(TOPIC_COMPLETED, event.orderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Lỗi publish order.completed {}: {}", event.orderId(), ex.getMessage());
                    }
                });
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {
        log.info("Publish order.cancelled: {}", event.orderId());
        kafkaTemplate.send(TOPIC_CANCELLED, event.orderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Lỗi publish order.cancelled {}: {}", event.orderId(), ex.getMessage());
                    }
                });
    }
}

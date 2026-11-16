package com.gymstore.notification.consumer;

import com.gymstore.common.event.OrderCancelledEvent;
import com.gymstore.common.event.OrderCompletedEvent;
import com.gymstore.notification.service.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumer sự kiện đơn hàng:
 * - order.completed → gửi email xác nhận.
 * - order.cancelled → gửi email thông báo hủy.
 *
 * @RetryableTopic: tự động retry với backoff (2→4→8s), sau 3 lần thất bại vào DLT.
 * Idempotent: IEmailService kiểm tra messageId trước khi gửi.
 */
@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final IEmailService emailService;

    public OrderEventConsumer(IEmailService emailService) {
        this.emailService = emailService;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = ".dlt"
    )
    @KafkaListener(topics = "order.completed", groupId = "notification-service",
                   containerFactory = "orderCompletedFactory")
    public void onOrderCompleted(OrderCompletedEvent event,
                                  @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log.info("Nhận order.completed: orderId={} email={}", event.orderId(), event.email());

        String email = event.email();
        if (email == null || email.isBlank()) {
            log.warn("Không có email cho orderId={}, bỏ qua", event.orderId());
            return;
        }

        Map<String, Object> vars = Map.of(
                "fullName", event.fullName() != null ? event.fullName() : "Khách hàng",
                "orderNumber", event.orderNumber(),
                "items", event.items(),
                "totalAmount", event.totalAmount(),
                "shippingAddress", event.shippingAddress(),
                "paymentMethod", event.paymentMethod(),
                "orderTime", event.orderTime()
        );

        emailService.sendTemplateEmail(
                messageKey,
                email,
                "GymStore - Xác nhận đơn hàng #" + event.orderNumber(),
                "order-confirmation",
                vars,
                event.orderId(), event.userId()
        );
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = ".dlt"
    )
    @KafkaListener(topics = "order.cancelled", groupId = "notification-service",
                   containerFactory = "orderCancelledFactory")
    public void onOrderCancelled(OrderCancelledEvent event,
                                  @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log.info("Nhận order.cancelled: orderId={}", event.orderId());

        String email = event.email();
        if (email == null || email.isBlank()) return;

        Map<String, Object> vars = Map.of(
                "fullName", event.fullName() != null ? event.fullName() : "Khách hàng",
                "orderNumber", event.orderNumber(),
                "cancelReason", event.cancelReason() != null ? event.cancelReason() : "Không rõ",
                "cancelTime", event.cancelTime(),
                "items", event.items(),
                "totalAmount", event.totalAmount()
        );

        emailService.sendTemplateEmail(
                messageKey + "-cancelled",
                email,
                "GymStore - Đơn hàng #" + event.orderNumber() + " đã bị hủy",
                "order-cancelled",
                vars,
                event.orderId(), event.userId()
        );
    }

    /** Dead Letter Topic handler: log lỗi sau khi hết lần retry. */
    @DltHandler
    public void dltHandler(Object event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("DLT message từ topic={}: {}", topic, event);
    }
}

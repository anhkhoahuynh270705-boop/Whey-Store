package com.gymstore.notification.config;

import com.gymstore.common.event.OrderCancelledEvent;
import com.gymstore.common.event.OrderCompletedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/** Cấu hình Kafka consumer với 2 factory riêng (typed) cho 2 event type. */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    private Map<String, Object> commonProps(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    @Bean
    public ConsumerFactory<String, OrderCompletedEvent> orderCompletedConsumerFactory() {
        JsonDeserializer<OrderCompletedEvent> deser = new JsonDeserializer<>(OrderCompletedEvent.class, false);
        return new DefaultKafkaConsumerFactory<>(commonProps("notification-service"),
                new StringDeserializer(), deser);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCompletedEvent> orderCompletedFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCompletedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCompletedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderCancelledEvent> orderCancelledConsumerFactory() {
        JsonDeserializer<OrderCancelledEvent> deser = new JsonDeserializer<>(OrderCancelledEvent.class, false);
        return new DefaultKafkaConsumerFactory<>(commonProps("notification-service"),
                new StringDeserializer(), deser);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCancelledEvent> orderCancelledFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCancelledEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCancelledConsumerFactory());
        return factory;
    }
}

package com.gymstore.cart.config;

import com.gymstore.cart.dto.response.CartItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestClient;

@Configuration
public class CartConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        Jackson2JsonRedisSerializer<CartItem> itemSerializer =
                new Jackson2JsonRedisSerializer<>(CartItem.class);
        template.setHashValueSerializer(itemSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RestClient productRestClient(@Value("${product-service.url:http://localhost:8081}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}

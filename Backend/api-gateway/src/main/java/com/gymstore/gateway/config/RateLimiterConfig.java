package com.gymstore.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userOrIpKeyResolver() {
        return exchange -> {
            var auth = exchange.getPrincipal();
            return auth.map(principal -> {
                        String name = principal.getName();
                        return (name != null && !name.isBlank()) ? name : resolveIp(exchange);
                    })
                    .switchIfEmpty(Mono.just(resolveIp(exchange)));
        };
    }

    private String resolveIp(ServerWebExchange exchange) {
        var remote = exchange.getRequest().getRemoteAddress();
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return remote != null ? remote.getAddress().getHostAddress() : "anonymous";
    }
}

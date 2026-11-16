package com.gymstore.gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Filter toàn cục: sinh/lan truyền correlation id (header X-Request-Id)
 * để trace request qua chuỗi gateway -> downstream service. Ghi log có trace id.
 */
@Component
@Order(-1)
public class CorrelationIdFilter implements GlobalFilter {

    public static final String HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        final String cid = correlationId;
        // Thêm header vào request downstream
        exchange.getRequest().mutate().header(HEADER, cid).build();
        // Expose để client dễ trace
        exchange.getResponse().getHeaders().add(HEADER, cid);
        return chain.filter(exchange)
                .doOnSubscribe(s -> MDC.put("traceId", cid))
                .doFinally(signal -> MDC.remove("traceId"));
    }
}

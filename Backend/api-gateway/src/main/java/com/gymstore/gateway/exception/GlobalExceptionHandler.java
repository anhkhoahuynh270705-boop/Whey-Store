package com.gymstore.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymstore.common.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        int status;
        String error;
        String message;

        if (ex instanceof ResponseStatusException rse) {
            status = rse.getStatusCode().value();
            error = rse.getReason() != null ? rse.getReason()
                    : HttpStatus.resolve(status) != null ? HttpStatus.resolve(status).getReasonPhrase() : "Error";
            message = rse.getMessage();
        } else if (ex instanceof IllegalArgumentException) {
            status = 400;
            error = "BAD_REQUEST";
            message = ex.getMessage();
        } else if (ex instanceof IllegalStateException) {
            status = 409;
            error = "CONFLICT";
            message = ex.getMessage();
        } else {
            log.error("Unhandled gateway error: ", ex);
            status = 500;
            error = "GW_006";
            message = "Lỗi gateway nội bộ";
        }

        String path = exchange.getRequest().getPath().value();
        ErrorResponse body = ErrorResponse.of(status, error, message, path);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = ("{\"status\":" + status + ",\"error\":\"" + error + "\"}").getBytes();
        }

        response.setStatusCode(HttpStatus.resolve(status) != null ? HttpStatus.resolve(status) : HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}


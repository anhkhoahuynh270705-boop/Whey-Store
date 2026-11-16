package com.gymstore.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway - cổng vào duy nhất cho frontend.
 * Định tuyến tới các backend service, xác thực JWT (Keycloak), CORS, rate limit.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

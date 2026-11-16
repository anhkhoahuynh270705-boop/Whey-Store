package com.gymstore.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** cart-service: giỏ hàng trong Redis (Hash + TTL), gộp giỏ guest khi đăng nhập. */
@SpringBootApplication
public class CartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }
}

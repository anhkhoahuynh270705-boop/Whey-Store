package com.gymstore.gateway.controller;

import com.gymstore.gateway.dto.request.RegisterRequest;
import com.gymstore.gateway.dto.response.UserAccountDto;
import com.gymstore.gateway.service.KeycloakUserService;
import com.gymstore.gateway.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    UserService userService;
    KeycloakUserService keycloakService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserAccountDto> register(@RequestBody RegisterRequest request) {
        return Mono.fromCallable(() -> userService.register(request));
    }

    @PostMapping("/logout")
    public Mono<Map<String, Object>> logout(@RequestBody Map<String, String> body) {
        return Mono.fromCallable(() -> {
            String refreshToken = body.get("refreshToken");
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new IllegalArgumentException("refreshToken không được để trống");
            }
            keycloakService.revokeSession(refreshToken);
            return Map.<String, Object>of("success", true, "message", "Đã đăng xuất thành công");
        });
    }
}

package com.gymstore.gateway.controller;

import com.gymstore.gateway.dto.request.AdminCreateUserRequest;
import com.gymstore.gateway.dto.response.UserAccountDto;
import com.gymstore.gateway.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserController {

    UserService userService;

    @GetMapping
    public Mono<List<UserAccountDto>> listAll() {
        return Mono.fromCallable(userService::listAll);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserAccountDto> create(@RequestBody AdminCreateUserRequest request) {
        return Mono.fromCallable(() -> userService.adminCreate(request));
    }

    @PatchMapping("/{id}/toggle-status")
    public Mono<UserAccountDto> toggleStatus(@PathVariable String id) {
        return Mono.fromCallable(() -> userService.toggleStatus(id));
    }

    /** Alias của toggle-status để tương thích với frontend cũ. */
    @PutMapping("/{id}/status")
    public Mono<UserAccountDto> updateStatus(@PathVariable String id) {
        return toggleStatus(id);
    }
}

package com.gymstore.gateway.config;

import com.gymstore.gateway.dto.request.RegisterRequest;
import com.gymstore.gateway.dto.request.AdminCreateUserRequest;
import com.gymstore.gateway.enums.UserRole;
import com.gymstore.gateway.service.KeycloakUserService;
import com.gymstore.gateway.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleAccountDbInitializer implements ApplicationRunner {

    private final UserService userService;
    private final KeycloakUserService keycloakService;

    record SampleAccount(String username, String email,
            String fullName, String password, UserRole role) {
    }

    private static final List<SampleAccount> SAMPLES = List.of(
            new SampleAccount("admin", "admin@gymstore.vn", "Admin GymStore", "admin", UserRole.ADMIN),
            new SampleAccount("staff", "staff@gymstore.vn", "Staff GymStore", "staff", UserRole.STAFF),
            new SampleAccount("customer", "customer@gymstore.vn", "Nguyen Van A", "customer", UserRole.CUSTOMER));

    @Override
    public void run(ApplicationArguments args) {
        Thread t = new Thread(this::seedWithRetry, "sample-account-seeder");
        t.setDaemon(true);
        t.start();
    }

    private void seedWithRetry() {
        int maxRetries = 15;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // Kiểm tra Keycloak đã sẵn sàng chưa
                if (!keycloakService.isAvailable())
                    throw new RuntimeException("Keycloak chưa sẵn sàng");

                for (SampleAccount acc : SAMPLES) {
                    seedOne(acc);
                }
                log.info("[Seeder] Đã đồng bộ tài khoản mẫu: admin, staff, customer");
                return;

            } catch (Exception e) {
                log.debug("[Seeder] Thử lần {}/{}: {}", attempt, maxRetries, e.getMessage());
                sleep(4000);
            }
        }
        log.warn("[Seeder] Không thể seed sau {} lần thử", maxRetries);
    }

    private void seedOne(SampleAccount acc) {
        try {
            if (acc.role() == UserRole.CUSTOMER) {
                RegisterRequest req = new RegisterRequest();
                req.setUsername(acc.username());
                req.setEmail(acc.email());
                req.setFullName(acc.fullName());
                req.setPassword(acc.password());
                userService.register(req);
            } else {
                AdminCreateUserRequest req = new AdminCreateUserRequest();
                req.setUsername(acc.username());
                req.setEmail(acc.email());
                req.setFullName(acc.fullName());
                req.setPassword(acc.password());
                req.setRole(acc.role());
                userService.adminCreate(req);
            }
            log.info("[Seeder] Tạo tài khoản '{}' ({})", acc.username(), acc.role());
        } catch (IllegalStateException e) {
            // Tài khoản đã tồn tại — bỏ qua
            log.debug("[Seeder] '{}' đã tồn tại, bỏ qua", acc.username());
        } catch (Exception e) {
            log.error("[Seeder] Lỗi seed '{}': {}", acc.username(), e.getMessage());
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

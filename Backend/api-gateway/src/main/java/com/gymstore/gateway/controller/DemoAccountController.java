package com.gymstore.gateway.controller;

import com.gymstore.gateway.dto.response.DemoAccountDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping({"/api/auth/demo-accounts", "/api/public/demo-accounts"})
public class DemoAccountController {

    private static final List<DemoAccountDto> DEMO_ACCOUNTS = List.of(
            new DemoAccountDto(
                    "Khách hàng",
                    "customer",
                    "customer",
                    "Mua sắm, giỏ hàng, xem đơn hàng",
                    "fas fa-shopping-bag",
                    "badge-customer"
            ),
            new DemoAccountDto(
                    "Nhân viên (Staff)",
                    "staff",
                    "staff",
                    "Quản lý đơn hàng, kho hàng",
                    "fas fa-user-tag",
                    "badge-staff"
            ),
            new DemoAccountDto(
                    "Quản trị viên (Admin)",
                    "admin",
                    "admin",
                    "Toàn quyền dashboard, sản phẩm, báo cáo",
                    "fas fa-shield-alt",
                    "badge-admin"
            )
    );

    @GetMapping
    public Flux<DemoAccountDto> getDemoAccounts() {
        return Flux.fromIterable(DEMO_ACCOUNTS);
    }
}

package com.gymstore.order.controller;

import com.gymstore.common.dto.response.PageResponse;
import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.common.dto.order.request.CreateOrderRequest;
import com.gymstore.common.dto.order.request.UpdateStatusRequest;
import com.gymstore.common.dto.order.response.CheckoutResponse;
import com.gymstore.common.dto.order.response.OrderDto;
import com.gymstore.order.dto.request.CheckoutRequest;
import com.gymstore.order.service.IOrderService;
import com.gymstore.order.service.WorkflowClient;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService orderService;
    private final WorkflowClient workflowClient;

    public OrderController(IOrderService orderService, WorkflowClient workflowClient) {
        this.orderService = orderService;
        this.workflowClient = workflowClient;
    }

    /**
     * Checkout: tạo đơn thông qua workflow-service (start Camunda process).
     */
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return workflowClient.startPurchaseProcess(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public PageResponse<OrderDto> list(
            @RequestParam(required = false, defaultValue = "false") boolean all,
            @PageableDefault(size = 10) Pageable pageable) {
        if (all) {
            return orderService.listAll(pageable);
        }
        return orderService.listForCurrentUser(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public OrderDto get(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF')")
    public OrderDto cancel(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @PostMapping("/internal")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createInternal(@Valid @RequestBody CreateOrderRequest req) {
        return orderService.createOrder(req);
    }

    @PutMapping("/internal/{id}/status")
    public OrderDto updateStatusInternal(@PathVariable Long id,
                                          @RequestBody UpdateStatusRequest req) {
        return orderService.updateStatus(id, req);
    }
}

package com.gymstore.inventory.controller;

import com.gymstore.common.contract.ReserveRequest;
import com.gymstore.common.contract.ReserveResult;
import com.gymstore.common.contract.StockActionRequest;
import com.gymstore.common.contract.StockActionResult;
import com.gymstore.inventory.dto.request.CreateInventoryItemRequest;
import com.gymstore.inventory.dto.response.InventoryItemDto;
import com.gymstore.inventory.service.IInventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final IInventoryService inventoryService;

    public InventoryController(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ---------- API công khai/STAFF (qua gateway) ----------
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public List<InventoryItemDto> list() {
        return inventoryService.list();
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public InventoryItemDto getByProductId(@PathVariable Long productId) {
        return inventoryService.getByProductId(productId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryItemDto createItem(@Valid @RequestBody CreateInventoryItemRequest req) {
        return inventoryService.createItem(req);
    }

    /** Nhập thêm hàng: cộng delta vào available. */
    @PutMapping("/{productId}/add")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public InventoryItemDto addStock(@PathVariable Long productId, @RequestParam int delta) {
        return inventoryService.addStock(productId, delta);
    }

    // API nội bộ cho workflow-service  
    @PostMapping("/internal/inventory/reserve")
    public ReserveResult reserve(@Valid @RequestBody ReserveRequest request) {
        return inventoryService.reserve(request);
    }

    @PostMapping("/internal/inventory/commit")
    public StockActionResult commit(@Valid @RequestBody StockActionRequest request) {
        return inventoryService.commit(request);
    }

    @PostMapping("/internal/inventory/release")
    public StockActionResult release(@Valid @RequestBody StockActionRequest request) {
        return inventoryService.release(request);
    }
}

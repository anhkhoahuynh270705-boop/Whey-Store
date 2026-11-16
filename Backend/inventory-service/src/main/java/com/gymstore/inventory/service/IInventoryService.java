package com.gymstore.inventory.service;

import com.gymstore.common.contract.ReserveRequest;
import com.gymstore.common.contract.ReserveResult;
import com.gymstore.common.contract.StockActionRequest;
import com.gymstore.common.contract.StockActionResult;
import com.gymstore.inventory.dto.request.CreateInventoryItemRequest;
import com.gymstore.inventory.dto.response.InventoryItemDto;

import java.util.List;

/**
 * Interface cho inventory service.
 */
public interface IInventoryService {

    /**
     * Danh sách tất cả inventory items.
     */
    List<InventoryItemDto> list();

    /**
     * Lấy inventory item theo product ID.
     */
    InventoryItemDto getByProductId(Long productId);

    /**
     * Tạo inventory item mới.
     */
    InventoryItemDto createItem(CreateInventoryItemRequest req);

    /**
     * Cộng thêm số lượng vào available (nhập hàng).
     */
    InventoryItemDto addStock(Long productId, int delta);

    /**
     * Đặt giữ hàng (reserve).
     */
    ReserveResult reserve(ReserveRequest request);

    /**
     * Trừ kho chính thức (commit).
     */
    StockActionResult commit(StockActionRequest request);

    /**
     * Hoàn kho (release/compensation).
     */
    StockActionResult release(StockActionRequest request);
}

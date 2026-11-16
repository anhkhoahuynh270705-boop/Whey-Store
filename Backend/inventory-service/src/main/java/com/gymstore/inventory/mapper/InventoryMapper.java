package com.gymstore.inventory.mapper;

import com.gymstore.common.contract.ReserveItem;
import com.gymstore.inventory.dto.request.CreateInventoryItemRequest;
import com.gymstore.inventory.dto.response.InventoryItemDto;
import com.gymstore.inventory.entity.InventoryItem;
import com.gymstore.inventory.entity.StockReservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper cho InventoryItem entity ↔ DTO.
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper {

    /** Entity → DTO response. */
    InventoryItemDto toDto(InventoryItem item);

    /**
     * Request → Entity mới.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservedQty", constant = "0")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    InventoryItem toEntity(CreateInventoryItemRequest request);

    /**
     * Cập nhật entity từ DTO (dùng khi update thủ công).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(InventoryItemDto dto, @MappingTarget InventoryItem entity);

    /**
     * Tạo bản ghi giữ kho từ orderId và ReserveItem.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "productId", source = "item.productId")
    @Mapping(target = "quantity", source = "item.quantity")
    @Mapping(target = "status", expression = "java(com.gymstore.inventory.enums.ReservationStatus.RESERVED)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StockReservation toReservation(String orderId, ReserveItem item);
}

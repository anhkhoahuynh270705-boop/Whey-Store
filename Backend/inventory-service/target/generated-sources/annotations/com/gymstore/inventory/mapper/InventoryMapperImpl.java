package com.gymstore.inventory.mapper;

import com.gymstore.common.contract.ReserveItem;
import com.gymstore.inventory.dto.request.CreateInventoryItemRequest;
import com.gymstore.inventory.dto.response.InventoryItemDto;
import com.gymstore.inventory.entity.InventoryItem;
import com.gymstore.inventory.entity.StockReservation;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-20T09:37:03+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public InventoryItemDto toDto(InventoryItem item) {
        if ( item == null ) {
            return null;
        }

        Long id = null;
        Long productId = null;
        String sku = null;
        int availableQty = 0;
        int reservedQty = 0;
        Long version = null;

        id = item.getId();
        productId = item.getProductId();
        sku = item.getSku();
        availableQty = item.getAvailableQty();
        reservedQty = item.getReservedQty();
        version = item.getVersion();

        InventoryItemDto inventoryItemDto = new InventoryItemDto( id, productId, sku, availableQty, reservedQty, version );

        return inventoryItemDto;
    }

    @Override
    public InventoryItem toEntity(CreateInventoryItemRequest request) {
        if ( request == null ) {
            return null;
        }

        InventoryItem inventoryItem = new InventoryItem();

        if ( request.availableQty() != null ) {
            inventoryItem.setAvailableQty( request.availableQty() );
        }
        inventoryItem.setProductId( request.productId() );
        inventoryItem.setSku( request.sku() );

        inventoryItem.setReservedQty( 0 );

        return inventoryItem;
    }

    @Override
    public void updateEntity(InventoryItemDto dto, InventoryItem entity) {
        if ( dto == null ) {
            return;
        }

        entity.setAvailableQty( dto.availableQty() );
        entity.setProductId( dto.productId() );
        entity.setReservedQty( dto.reservedQty() );
        entity.setSku( dto.sku() );
    }

    @Override
    public StockReservation toReservation(String orderId, ReserveItem item) {
        if ( orderId == null && item == null ) {
            return null;
        }

        StockReservation stockReservation = new StockReservation();

        if ( item != null ) {
            stockReservation.setProductId( item.productId() );
            stockReservation.setQuantity( item.quantity() );
        }
        stockReservation.setOrderId( orderId );
        stockReservation.setStatus( com.gymstore.inventory.enums.ReservationStatus.RESERVED );

        return stockReservation;
    }
}

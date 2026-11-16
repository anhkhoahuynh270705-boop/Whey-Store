package com.gymstore.order.mapper;

import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.common.dto.order.request.CreateOrderRequest;
import com.gymstore.common.dto.order.response.OrderDto;
import com.gymstore.common.dto.order.response.OrderItemDto;
import com.gymstore.common.dto.order.response.OrderStatusHistoryDto;
import com.gymstore.order.entity.Order;
import com.gymstore.order.entity.OrderItem;
import com.gymstore.order.entity.OrderStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderItemDto toItemDto(OrderItem item);

    /**
     * OrderStatusHistory entity → OrderStatusHistoryDto.
     */
    @Mapping(target = "status", expression = "java(history.getStatus().name())")
    OrderStatusHistoryDto toHistoryDto(OrderStatusHistory history);

    /**
     * Order entity → OrderDto (items, statusHistory và các trường trùng tên
     * được tự động map).
     */
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderDto toDto(Order order);

    /**
     * CreateOrderRequest → Order entity (PENDING state).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", source = "orderNumber")
    @Mapping(target = "status", expression = "java(com.gymstore.order.enums.OrderStatus.PENDING)")
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "processInstanceId", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "cancelReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(CreateOrderRequest req, String orderNumber);

    /**
     * CheckoutItem → OrderItem entity (tính subtotal tự động).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "subtotal", expression = "java(item.unitPrice().multiply(java.math.BigDecimal.valueOf(item.quantity())))")
    OrderItem toItemEntity(CheckoutItem item);
}

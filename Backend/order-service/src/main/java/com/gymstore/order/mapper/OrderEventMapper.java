package com.gymstore.order.mapper;

import com.gymstore.common.event.OrderCancelledEvent;
import com.gymstore.common.event.OrderCompletedEvent;
import com.gymstore.common.event.OrderItemEvent;
import com.gymstore.common.event.ShippingAddressEvent;
import com.gymstore.order.entity.Order;
import com.gymstore.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderEventMapper {

    OrderItemEvent toItemEvent(OrderItem item);

    /**
     * Order entity → OrderCompletedEvent.
     */
    @Mapping(target = "orderId", expression = "java(String.valueOf(order.getId()))")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "shippingAddress", expression = "java(toShippingEvent(order.getShippingAddress()))")
    @Mapping(target = "orderTime", expression = "java(java.time.Instant.now())")
    OrderCompletedEvent toCompletedEvent(Order order);

    /**
     * Order entity → OrderCancelledEvent.
     */
    @Mapping(target = "orderId", expression = "java(String.valueOf(order.getId()))")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "cancelTime", expression = "java(java.time.Instant.now())")
    OrderCancelledEvent toCancelledEvent(Order order);

    /**
     * Chuyển chuỗi địa chỉ thành ShippingAddressEvent (addressLine only).
     */
    default ShippingAddressEvent toShippingEvent(String shippingAddress) {
        return new ShippingAddressEvent(null, null, shippingAddress, null, null, null, null);
    }
}

package com.gymstore.order.mapper;

import com.gymstore.common.event.OrderCancelledEvent;
import com.gymstore.common.event.OrderCompletedEvent;
import com.gymstore.common.event.OrderItemEvent;
import com.gymstore.common.event.ShippingAddressEvent;
import com.gymstore.order.entity.Order;
import com.gymstore.order.entity.OrderItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-16T13:36:08+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class OrderEventMapperImpl implements OrderEventMapper {

    @Override
    public OrderItemEvent toItemEvent(OrderItem item) {
        if ( item == null ) {
            return null;
        }

        Long productId = null;
        String productName = null;
        int quantity = 0;
        BigDecimal unitPrice = null;
        BigDecimal subtotal = null;

        productId = item.getProductId();
        productName = item.getProductName();
        quantity = item.getQuantity();
        unitPrice = item.getUnitPrice();
        subtotal = item.getSubtotal();

        OrderItemEvent orderItemEvent = new OrderItemEvent( productId, productName, quantity, unitPrice, subtotal );

        return orderItemEvent;
    }

    @Override
    public OrderCompletedEvent toCompletedEvent(Order order) {
        if ( order == null ) {
            return null;
        }

        String orderNumber = null;
        String userId = null;
        List<OrderItemEvent> items = null;
        BigDecimal totalAmount = null;
        String paymentMethod = null;

        orderNumber = order.getOrderNumber();
        userId = order.getUserId();
        items = orderItemListToOrderItemEventList( order.getItems() );
        totalAmount = order.getTotalAmount();
        paymentMethod = order.getPaymentMethod();

        String orderId = String.valueOf(order.getId());
        String email = null;
        String fullName = null;
        ShippingAddressEvent shippingAddress = toShippingEvent(order.getShippingAddress());
        Instant orderTime = java.time.Instant.now();

        OrderCompletedEvent orderCompletedEvent = new OrderCompletedEvent( orderId, orderNumber, userId, email, fullName, items, totalAmount, shippingAddress, paymentMethod, orderTime );

        return orderCompletedEvent;
    }

    @Override
    public OrderCancelledEvent toCancelledEvent(Order order) {
        if ( order == null ) {
            return null;
        }

        String orderNumber = null;
        String userId = null;
        List<OrderItemEvent> items = null;
        BigDecimal totalAmount = null;
        String cancelReason = null;

        orderNumber = order.getOrderNumber();
        userId = order.getUserId();
        items = orderItemListToOrderItemEventList( order.getItems() );
        totalAmount = order.getTotalAmount();
        cancelReason = order.getCancelReason();

        String orderId = String.valueOf(order.getId());
        String email = null;
        String fullName = null;
        Instant cancelTime = java.time.Instant.now();

        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent( orderId, orderNumber, userId, email, fullName, items, totalAmount, cancelReason, cancelTime );

        return orderCancelledEvent;
    }

    protected List<OrderItemEvent> orderItemListToOrderItemEventList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemEvent> list1 = new ArrayList<OrderItemEvent>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toItemEvent( orderItem ) );
        }

        return list1;
    }
}

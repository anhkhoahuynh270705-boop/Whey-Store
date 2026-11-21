package com.gymstore.order.mapper;

import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.common.dto.order.request.CreateOrderRequest;
import com.gymstore.common.dto.order.response.OrderDto;
import com.gymstore.common.dto.order.response.OrderItemDto;
import com.gymstore.common.dto.order.response.OrderStatusHistoryDto;
import com.gymstore.order.entity.Order;
import com.gymstore.order.entity.OrderItem;
import com.gymstore.order.entity.OrderStatusHistory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-20T09:37:07+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderItemDto toItemDto(OrderItem item) {
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

        OrderItemDto orderItemDto = new OrderItemDto( productId, productName, quantity, unitPrice, subtotal );

        return orderItemDto;
    }

    @Override
    public OrderStatusHistoryDto toHistoryDto(OrderStatusHistory history) {
        if ( history == null ) {
            return null;
        }

        String note = null;
        LocalDateTime createdAt = null;

        note = history.getNote();
        createdAt = history.getCreatedAt();

        String status = history.getStatus().name();

        OrderStatusHistoryDto orderStatusHistoryDto = new OrderStatusHistoryDto( status, note, createdAt );

        return orderStatusHistoryDto;
    }

    @Override
    public OrderDto toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        Long id = null;
        String orderNumber = null;
        String userId = null;
        BigDecimal totalAmount = null;
        String shippingAddress = null;
        String paymentMethod = null;
        String processInstanceId = null;
        String cancelReason = null;
        List<OrderItemDto> items = null;
        List<OrderStatusHistoryDto> statusHistory = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;
        LocalDateTime completedAt = null;
        LocalDateTime cancelledAt = null;

        id = order.getId();
        orderNumber = order.getOrderNumber();
        userId = order.getUserId();
        totalAmount = order.getTotalAmount();
        shippingAddress = order.getShippingAddress();
        paymentMethod = order.getPaymentMethod();
        processInstanceId = order.getProcessInstanceId();
        cancelReason = order.getCancelReason();
        items = orderItemListToOrderItemDtoList( order.getItems() );
        statusHistory = orderStatusHistoryListToOrderStatusHistoryDtoList( order.getStatusHistory() );
        createdAt = order.getCreatedAt();
        updatedAt = order.getUpdatedAt();
        completedAt = order.getCompletedAt();
        cancelledAt = order.getCancelledAt();

        String status = order.getStatus().name();

        OrderDto orderDto = new OrderDto( id, orderNumber, userId, status, totalAmount, shippingAddress, paymentMethod, processInstanceId, cancelReason, items, statusHistory, createdAt, updatedAt, completedAt, cancelledAt );

        return orderDto;
    }

    @Override
    public Order toEntity(CreateOrderRequest req, String orderNumber) {
        if ( req == null && orderNumber == null ) {
            return null;
        }

        Order order = new Order();

        if ( req != null ) {
            order.setPaymentMethod( req.paymentMethod() );
            order.setShippingAddress( req.shippingAddress() );
            order.setTotalAmount( req.totalAmount() );
            order.setUserId( req.userId() );
        }
        order.setOrderNumber( orderNumber );
        order.setStatus( com.gymstore.order.enums.OrderStatus.PENDING );

        return order;
    }

    @Override
    public OrderItem toItemEntity(CheckoutItem item) {
        if ( item == null ) {
            return null;
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setProductId( item.productId() );
        orderItem.setProductName( item.productName() );
        if ( item.quantity() != null ) {
            orderItem.setQuantity( item.quantity() );
        }
        orderItem.setUnitPrice( item.unitPrice() );

        orderItem.setSubtotal( item.unitPrice().multiply(java.math.BigDecimal.valueOf(item.quantity())) );

        return orderItem;
    }

    protected List<OrderItemDto> orderItemListToOrderItemDtoList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemDto> list1 = new ArrayList<OrderItemDto>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toItemDto( orderItem ) );
        }

        return list1;
    }

    protected List<OrderStatusHistoryDto> orderStatusHistoryListToOrderStatusHistoryDtoList(List<OrderStatusHistory> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderStatusHistoryDto> list1 = new ArrayList<OrderStatusHistoryDto>( list.size() );
        for ( OrderStatusHistory orderStatusHistory : list ) {
            list1.add( toHistoryDto( orderStatusHistory ) );
        }

        return list1;
    }
}

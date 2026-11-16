package com.gymstore.order.service.impl;

import com.gymstore.common.dto.response.PageResponse;
import com.gymstore.common.exception.ConflictException;
import com.gymstore.common.exception.ForbiddenException;
import com.gymstore.common.exception.ResourceNotFoundException;
import com.gymstore.common.security.JwtUser;
import com.gymstore.common.dto.order.request.CheckoutItem;
import com.gymstore.common.dto.order.request.CreateOrderRequest;
import com.gymstore.common.dto.order.request.UpdateStatusRequest;
import com.gymstore.common.dto.order.response.OrderDto;
import com.gymstore.order.entity.Order;
import com.gymstore.order.entity.OrderItem;
import com.gymstore.order.entity.OrderStatusHistory;
import com.gymstore.order.enums.OrderStatus;
import com.gymstore.order.kafka.OrderEventProducer;
import com.gymstore.order.mapper.OrderEventMapper;
import com.gymstore.order.mapper.OrderMapper;
import com.gymstore.order.repository.OrderRepository;
import com.gymstore.order.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements IOrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;
    private final OrderMapper mapper;
    private final OrderEventMapper eventMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderEventProducer eventProducer,
                            OrderMapper mapper,
                            OrderEventMapper eventMapper) {
        this.orderRepository = orderRepository;
        this.eventProducer = eventProducer;
        this.mapper = mapper;
        this.eventMapper = eventMapper;
    }

    @Override
    public PageResponse<OrderDto> listForCurrentUser(Pageable pageable) {
        String userId = JwtUser.userId();
        Page<Order> page = orderRepository.findByUserId(userId, pageable);
        return PageResponse.from(page.map(mapper::toDto));
    }

    @Override
    public PageResponse<OrderDto> listAll(Pageable pageable) {
        return PageResponse.from(orderRepository.findAll(pageable).map(mapper::toDto));
    }

    @Override
    public OrderDto getOrder(Long id) {
        Order order = orderRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", id));
        // CUSTOMER chỉ xem đơn của mình
        String userId = JwtUser.userId();
        if (userId != null && !order.getUserId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền xem đơn này");
        }
        return mapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long id) {
        Order order = orderRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", id));
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.RESERVED) {
            throw new ConflictException("Chỉ huỷ được đơn PENDING hoặc RESERVED");
        }
        return updateStatus(id, new UpdateStatusRequest("CANCELLED", "Khách hàng yêu cầu huỷ"));
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest req) {
        String orderNumber = "GS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = mapper.toEntity(req, orderNumber);

        for (CheckoutItem ci : req.items()) {
            OrderItem item = mapper.toItemEntity(ci);
            order.addItem(item);
        }

        OrderStatusHistory history = new OrderStatusHistory();
        history.setStatus(OrderStatus.PENDING);
        history.setNote("Đơn hàng vừa tạo");
        order.addHistory(history);

        order = orderRepository.save(order);
        log.info("Tạo đơn {} cho user {}", order.getOrderNumber(), order.getUserId());
        return mapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto updateStatus(Long id, UpdateStatusRequest req) {
        Order order = orderRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", id));
        OrderStatus newStatus = OrderStatus.valueOf(req.status());
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
        } else if (newStatus == OrderStatus.CANCELLED) {
            order.setCancelledAt(LocalDateTime.now());
            if (req.note() != null) order.setCancelReason(req.note());
        }

        OrderStatusHistory history = new OrderStatusHistory();
        history.setStatus(newStatus);
        history.setNote(req.note());
        order.addHistory(history);

        order = orderRepository.save(order);

        // Publish sự kiện Kafka
        if (newStatus == OrderStatus.COMPLETED) {
            eventProducer.publishOrderCompleted(eventMapper.toCompletedEvent(order));
        } else if (newStatus == OrderStatus.CANCELLED) {
            eventProducer.publishOrderCancelled(eventMapper.toCancelledEvent(order));
        }
        return mapper.toDto(order);
    }

    @Override
    @Transactional
    public void setProcessInstanceId(Long orderId, String processInstanceId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", orderId));
        order.setProcessInstanceId(processInstanceId);
        orderRepository.save(order);
    }
}

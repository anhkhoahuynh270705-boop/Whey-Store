package com.gymstore.order.service;

import com.gymstore.common.dto.response.PageResponse;
import com.gymstore.common.dto.order.request.CreateOrderRequest;
import com.gymstore.common.dto.order.response.OrderDto;
import com.gymstore.common.dto.order.request.UpdateStatusRequest;
import org.springframework.data.domain.Pageable;

/**
 * Interface cho order service.
 */
public interface IOrderService {

    /**
     * Danh sách đơn hàng của user hiện tại.
     */
    PageResponse<OrderDto> listForCurrentUser(Pageable pageable);

    /**
     * Danh sách tất cả đơn hàng (admin).
     */
    PageResponse<OrderDto> listAll(Pageable pageable);

    /**
     * Chi tiết đơn hàng.
     */
    OrderDto getOrder(Long id);

    /**
     * Hủy đơn hàng.
     */
    OrderDto cancelOrder(Long id);

    /**
     * Tạo đơn hàng mới (internal API từ workflow-service).
     */
    OrderDto createOrder(CreateOrderRequest req);

    /**
     * Cập nhật trạng thái đơn hàng (internal API).
     */
    OrderDto updateStatus(Long id, UpdateStatusRequest req);

    /**
     * Set process instance ID cho đơn hàng.
     */
    void setProcessInstanceId(Long orderId, String processInstanceId);
}

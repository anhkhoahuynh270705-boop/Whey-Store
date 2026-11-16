package com.gymstore.order.repository;

import com.gymstore.order.entity.Order;
import com.gymstore.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(String userId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("""
            select o from Order o
            left join fetch o.items
            left join fetch o.statusHistory
            where o.id = :id
            """)
    Optional<Order> findDetailById(@Param("id") Long id);

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByProcessInstanceId(String processInstanceId);
}

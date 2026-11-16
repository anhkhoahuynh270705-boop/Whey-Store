package com.gymstore.inventory.entity;

import com.gymstore.inventory.enums.StockMovementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** Nhật ký biến động kho (audit). */
@Entity
@Table(name = "stock_movements",
        indexes = {
            @Index(name = "idx_movement_product", columnList = "product_id"),
            @Index(name = "idx_movement_order", columnList = "order_id")
        })
@Getter
@Setter
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "order_id")
    private String orderId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockMovementType type;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public StockMovement() {}

    public StockMovement(Long productId, String orderId, int quantity, StockMovementType type) {
        this.productId = productId;
        this.orderId = orderId;
        this.quantity = quantity;
        this.type = type;
    }
}

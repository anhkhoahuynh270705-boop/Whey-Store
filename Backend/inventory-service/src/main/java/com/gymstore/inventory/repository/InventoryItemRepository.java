package com.gymstore.inventory.repository;

import com.gymstore.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    List<InventoryItem> findAllByOrderByProductIdAsc();
}

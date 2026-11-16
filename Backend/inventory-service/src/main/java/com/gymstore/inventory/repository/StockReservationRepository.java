package com.gymstore.inventory.repository;

import com.gymstore.inventory.enums.ReservationStatus;
import com.gymstore.inventory.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    List<StockReservation> findByOrderId(String orderId);

    List<StockReservation> findByOrderIdAndStatus(String orderId, ReservationStatus status);
}

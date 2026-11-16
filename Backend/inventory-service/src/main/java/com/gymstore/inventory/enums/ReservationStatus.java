package com.gymstore.inventory.enums;

/** Trạng thái đặt giữ hàng. */
public enum ReservationStatus {
    RESERVED,    // đang giữ (chưa thanh toán)
    COMMITTED,    // đã trừ kho chính thức
    RELEASED,     // đã hoàn kho
    EXPIRED       // hết hạn (timer huỷ)
}

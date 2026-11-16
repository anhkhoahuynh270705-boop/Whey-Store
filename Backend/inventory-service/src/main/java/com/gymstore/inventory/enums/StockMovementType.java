package com.gymstore.inventory.enums;

/** Loại biến động kho. */
public enum StockMovementType {
    RESERVE,   // giữ hàng
    COMMIT,    // trừ kho chính thức
    RELEASE    // hoàn kho
}

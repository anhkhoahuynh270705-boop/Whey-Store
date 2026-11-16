package com.gymstore.common.event;

/** Địa chỉ giao hàng. */
public record ShippingAddressEvent(
        String fullName,
        String phone,
        String addressLine,
        String ward,
        String district,
        String city,
        String note
) {}

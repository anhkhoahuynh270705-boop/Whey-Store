package com.gymstore.common.dto.order.request;

import jakarta.validation.constraints.NotBlank;

public record ShippingAddressRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
        @NotBlank String addressLine,
        String ward,
        String district,
        @NotBlank String city,
        String note
) {
    /** Nối thành chuỗi địa chỉ để lưu DB. */
    public String toOneLine() {
        return String.join(", ", addressLine,
                ward != null ? ward : "",
                district != null ? district : "",
                city).replaceAll(", ,", ",");
    }
}

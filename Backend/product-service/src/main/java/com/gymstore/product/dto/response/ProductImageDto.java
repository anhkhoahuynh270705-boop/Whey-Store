package com.gymstore.product.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductImageDto {

    private Long id;
    private String imageUrl;

    @JsonProperty("isPrimary")
    private boolean isPrimary;

    public ProductImageDto() {
    }

    public ProductImageDto(Long id, String imageUrl, boolean isPrimary) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
    }

    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @JsonProperty("isPrimary")
    public boolean isPrimary() {
        return isPrimary;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    @JsonProperty("isPrimary")
    public void setIsPrimary(boolean v) {
        this.isPrimary = v;
    }
}

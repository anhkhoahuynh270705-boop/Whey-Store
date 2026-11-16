package com.gymstore.product.repository;

import com.gymstore.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /** Lấy ảnh đại diện cho nhiều sản phẩm cùng lúc (tránh N+1). */
    @Query("""
            select i.product.id as productId, i.imageUrl as imageUrl
            from ProductImage i
            where i.isPrimary = true and i.product.id in :ids
            """)
    List<PrimaryImageProjection> findPrimaryImagesByProductIdIn(@Param("ids") Collection<Long> ids);

    interface PrimaryImageProjection {
        Long getProductId();
        String getImageUrl();
    }
}

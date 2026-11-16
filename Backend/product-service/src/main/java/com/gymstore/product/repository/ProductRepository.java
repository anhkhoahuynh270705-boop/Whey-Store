package com.gymstore.product.repository;

import com.gymstore.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("""
            select p from Product p
            left join fetch p.category
            left join fetch p.brand
            left join fetch p.images
            where p.id = :id
            """)
    Optional<Product> findDetailById(@Param("id") Long id);

    @Query("""
            select p from Product p
            left join fetch p.category
            left join fetch p.brand
            left join fetch p.images
            where p.slug = :slug
            """)
    Optional<Product> findDetailBySlug(@Param("slug") String slug);

    @Query("select count(p) > 0 from Product p where p.category.id = :id")
    boolean existsAnyProductForCategory(@Param("id") Long id);

    @Query("select count(p) > 0 from Product p where p.brand.id = :id")
    boolean existsAnyProductForBrand(@Param("id") Long id);
}

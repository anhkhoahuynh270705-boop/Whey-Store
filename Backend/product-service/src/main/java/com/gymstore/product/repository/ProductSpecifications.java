package com.gymstore.product.repository;

import com.gymstore.product.dto.request.ProductFilter;
import com.gymstore.product.entity.Product;
import com.gymstore.product.enums.ProductStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> withFilters(ProductFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            } else if (!filter.includeInactive()) {
                predicates.add(cb.equal(root.get("status"), ProductStatus.ACTIVE));
            }
            if (filter.search() != null && !filter.search().isBlank()) {
                predicates.add(cb.or(
                        likeIgnoreCase(cb, root.<String>get("name"), filter.search()),
                        likeIgnoreCase(cb, root.<String>get("description"), filter.search())
                ));
            }
            if (filter.categoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.categoryId()));
            }
            if (filter.brandId() != null) {
                predicates.add(cb.equal(root.get("brand").get("id"), filter.brandId()));
            }
            if (filter.flavor() != null && !filter.flavor().isBlank()) {
                predicates.add(likeIgnoreCase(cb, root.<String>get("flavor"), filter.flavor()));
            }
            if (filter.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
            }
            if (filter.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate likeIgnoreCase(CriteriaBuilder cb, Path<String> path, String value) {
        return cb.like(cb.lower(path), "%" + value.toLowerCase() + "%");
    }
}

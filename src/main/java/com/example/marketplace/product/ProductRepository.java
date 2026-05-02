package com.example.marketplace.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndSellerId(Long id, Long sellerId);

    List<Product> findBySellerId(Long sellerId);

    List<Product> findByStatus(ProductStatus status);
}

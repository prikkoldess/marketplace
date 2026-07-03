package com.example.marketplace.order.orderGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long> {
    List<OrderGroup> findByBuyerId(Long buyerId);

    Optional<OrderGroup> findById(UUID id);
}

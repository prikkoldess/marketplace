package com.example.marketplace.order.orderGroup;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long> {
    List<OrderGroup> findByBuyerId(Long buyerId);
}

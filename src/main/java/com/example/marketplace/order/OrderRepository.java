package com.example.marketplace.order;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);

    List<Order> findByOrderGroupIdAndBuyerId(UUID orderGroupId, Long buyerId);

    @EntityGraph(attributePaths = { "items" })
    @Query("SELECT o FROM Order o WHERE o.merchant.id = :merchantId")
    List<Order> findOrdersByMerchantId(@Param("merchantId") UUID merchantId);
}

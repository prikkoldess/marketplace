package com.example.marketplace.order;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);

    List<Order> findByCheckoutGroupIdAndBuyerId(UUID checkoutGroupId, Long buyerId);

    // ОБНОВЛЕНО: Запрос стал короче и быстрее
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.seller.id = :sellerId")
    List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);
}

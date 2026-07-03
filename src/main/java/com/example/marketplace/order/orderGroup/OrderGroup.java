package com.example.marketplace.order.orderGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.marketplace.order.Order;
import com.example.marketplace.order.OrderStatus;
import com.example.marketplace.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_groups")
@Getter
@NoArgsConstructor
public class OrderGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @JoinColumn(name = "buyer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User buyer;

    @OneToMany(mappedBy = "orderGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    public OrderGroup(User buyer) {
        this.buyer = buyer;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
        order.setOrderGroup(this);
        this.totalAmount = this.totalAmount.add(order.getTotalAmount());
    }

    public void changeOrderStatus(OrderStatus newStatus) {
        this.status = newStatus;

        for (Order order : orders) {
            order.changeStatus(newStatus);
        }
    }

    public void cancelOrder() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("The order has already been cancelled.");
        }
        this.status = OrderStatus.CANCELLED;

        for (Order order : this.orders) {
            order.cancelOrders();

        }

    }
}

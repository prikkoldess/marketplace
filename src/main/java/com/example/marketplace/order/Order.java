package com.example.marketplace.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.example.marketplace.Merchant.Merchant;
import com.example.marketplace.order.orderGroup.OrderGroup;
import com.example.marketplace.order.orderItem.OrderItem;
import com.example.marketplace.product.Product;
import com.example.marketplace.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_group_id")
    @JsonIgnore
    private OrderGroup orderGroup;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    public Order(User buyer, Merchant merchant) {
        this.buyer = buyer;
        this.merchant = merchant;
    }

    public void addToOrder(Integer quantity, Product product) {
        OrderItem item = new OrderItem(quantity, product, this);
        this.items.add(item);

        BigDecimal sum = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(quantity));
        this.totalAmount = this.totalAmount.add(sum);

    }

    public void setOrderGroup(OrderGroup orderGroup) {
        this.orderGroup = orderGroup;
    }

    public void changeStatus(OrderStatus requestStatus) {
        if (this.status == OrderStatus.DELIVERED || this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("An order that has already been shipped or delivered cannot be cancelled.");
        }
        this.status = requestStatus;
    }

    public void cancelOrders() {
        if (this.status == OrderStatus.DELIVERED || this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("An order that has already been shipped or delivered cannot be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;

        for (OrderItem item : this.items) {
            item.returnToTheWarehouse();
        }

    }

}

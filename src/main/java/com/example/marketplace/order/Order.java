package com.example.marketplace.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.basketitem.BasketItem;
import com.example.marketplace.product.Product;
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

    @Column(name = "checkout_group_id")
    private UUID checkoutGroupId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public Order(User buyer) {
        this.buyer = buyer;
    }

    public void addToOrder(Integer quantity, Product product) {
        OrderItem item = new OrderItem(quantity, product, this);
        this.items.add(item);

        BigDecimal sum = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(quantity));
        this.totalAmount = this.totalAmount.add(sum);

    }

    public void setCheckoutGroupId(UUID checkoutGroupId) {
        this.checkoutGroupId = checkoutGroupId;
    }

    public void changeStatus(OrderStatus requestStatus) {
        this.status = requestStatus;
    }
}

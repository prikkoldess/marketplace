package com.example.marketplace.basket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.marketplace.basket.basketitem.BasketItem;
import com.example.marketplace.product.Product;
import com.example.marketplace.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "basket")
@Getter
@NoArgsConstructor
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", unique = true)
    private User buyer;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasketItem> items = new ArrayList<>();

    public Basket(User buyer) {
        this.buyer = buyer;

    }

    public void addItem(Product product, Integer quantity) {
        for (BasketItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.increaseQuantity(quantity);
                return;
            }
        }
        BasketItem newItem = new BasketItem(this, product, quantity);
        this.items.add(newItem);
    }

    public void clearBasket() {
        this.items.clear();
    }

    public BigDecimal totalCost() {
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void deleteProductItem(Long productId) {
        boolean delete = this.items.removeIf(item -> item.getProduct().getId().equals(productId));

        if (!delete) {
            throw new IllegalArgumentException("The product was not found in the basket");
        }
    }
}

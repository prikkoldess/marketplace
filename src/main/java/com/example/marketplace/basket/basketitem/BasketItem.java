package com.example.marketplace.basket.basketitem;

import com.example.marketplace.basket.Basket;
import com.example.marketplace.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@NoArgsConstructor
@Getter
@Setter
public class BasketItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basket_id")
    private Basket basket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    public BasketItem(Basket basket, Product product, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("The quantity must be greater than zero.");
        }
        this.basket = basket;
        this.product = product;
        this.quantity = quantity;
    }

    public void increaseQuantity(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Cannot add a negative or zero quantity.");
        }
        this.quantity += amount;
    }
}

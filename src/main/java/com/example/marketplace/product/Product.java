package com.example.marketplace.product;

import java.math.BigDecimal;

import com.example.marketplace.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    public Product(String title, BigDecimal price, Integer quantity, User seller) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.seller = seller;

    }

    public static Product createProduct(String title, BigDecimal price, Integer quantity, User seller) {
        return new Product(title,
                price,
                quantity,
                seller);

    }

    public void hideProduct() {
        this.status = ProductStatus.BLOCKED;
    }

    public void unlockProduct() {
        this.status = ProductStatus.ACTIVE;
    }

    public void updateProduct(Integer quantity, BigDecimal price) {

        if (quantity != null) {
            if (quantity < 0)
                throw new IllegalArgumentException();
            this.quantity = quantity;
        }

        if (price != null) {
            if (price.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException();
            this.price = price;
        }
    }

}

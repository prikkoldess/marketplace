package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductStatus;
import com.example.marketplace.user.User;

public class ProductTest {
    @Test
    void createProduct() {
        User seller = new User();
        seller.setId(1L);

        Product product = new Product("Apple",
                new BigDecimal("50.00"),
                10,
                seller);

        assertEquals("Apple", product.getTitle());
        assertEquals(new BigDecimal("50.00"), product.getPrice());
        assertEquals(seller, product.getSeller());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    void hideProduct() {
        User seller = new User();
        seller.setId(1L);

        Product product = new Product("Apple",
                new BigDecimal("50.00"),
                10,
                seller);
        product.hideProduct();

        assertEquals(ProductStatus.BLOCKED, product.getStatus());
    }

    @Test
    void updateProduct() {
        User seller = new User();
        seller.setId(1L);

        Product product = new Product("Apple",
                new BigDecimal("50.00"),
                10,
                seller);

        product.updateProduct(null, new BigDecimal("100"));

        assertEquals(new BigDecimal("100"), product.getPrice());
        assertEquals(10, product.getQuantity());
    }

    @Test
    void unlockProduct() {
        User seller = new User();
        seller.setId(1L);

        Product product = new Product("Apple",
                new BigDecimal("50.00"),
                10,
                seller);

        product.hideProduct();
        assertEquals(ProductStatus.BLOCKED, product.getStatus());

        product.unlockProduct();
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    void decreaseQuantity() {
        User user = new User();
        user.setId(1L);
        Product product = new Product("Apple", new BigDecimal("30"), 20, user);
        product.decreaseQuantity(10);
        assertEquals(10, product.getQuantity());

    }
}

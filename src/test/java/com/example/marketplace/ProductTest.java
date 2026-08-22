package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.marketplace.merchant.Merchant;
import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductStatus;
import com.example.marketplace.user.User;

public class ProductTest {
    @Test
    void createProduct() {
        User seller = new User();
        seller.setId(1L);
        Merchant merchant = mock(Merchant.class);
        Product product = new Product("Apple",
                new BigDecimal("50.00"),
                10, merchant);

        assertEquals("Apple", product.getTitle());
        assertEquals(new BigDecimal("50.00"), product.getPrice());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    void hideProduct() {
        User seller = new User();
        seller.setId(1L);
        Merchant merchant = mock(Merchant.class);
        Product product = new Product("Apple",
                new BigDecimal("50.00"),
                10,
                merchant);
        product.hideProduct();

        assertEquals(ProductStatus.BLOCKED, product.getStatus());
    }

    @Test
    void updateProduct() {
        User seller = new User();
        seller.setId(1L);
        Merchant merchant = mock(Merchant.class);
        Product product = new Product("Apple",
                new BigDecimal("50.00"),
                10, merchant);

        product.updateProduct(null, new BigDecimal("100"));

        assertEquals(new BigDecimal("100"), product.getPrice());
        assertEquals(10, product.getQuantity());
    }

    @Test
    void unlockProduct() {
        User seller = new User();
        seller.setId(1L);
        Merchant merchant = mock(Merchant.class);
        Product product = new Product("Apple",
                new BigDecimal("50.00"),
                10,
                merchant);

        product.hideProduct();
        assertEquals(ProductStatus.BLOCKED, product.getStatus());

        product.unlockProduct();
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    void decreaseQuantity() {
        Merchant merchant = mock(Merchant.class);
        User user = new User();
        user.setId(1L);
        Product product = new Product("Apple", new BigDecimal("30"), 20, merchant);
        product.decreaseQuantity(10);
        assertEquals(10, product.getQuantity());

    }
}

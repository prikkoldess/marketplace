package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.marketplace.basket.Basket;

import com.example.marketplace.product.Product;
import com.example.marketplace.user.User;

public class BasketTest {

    private Basket basket;
    private User mockUser;

    @BeforeEach
    void setaUp() {
        mockUser = mock(User.class);
        basket = new Basket(mockUser);

    }

    @Test
    void addItem() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(1L);

        basket.addItem(product, 2);
        basket.addItem(product, 3);

        assertEquals(1, basket.getItems().size());
        assertEquals(5, basket.getItems().get(0).getQuantity());
    }

    @Test
    void clearBasket() {
        Product product = mock(Product.class);
        basket.addItem(product, 2);
        assertEquals(1, basket.getItems().size());

        basket.clearBasket();

        assertEquals(0, basket.getItems().size());
    }

    @Test
    void totalCost() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(1L);
        when(product.getPrice()).thenReturn(new BigDecimal("100.00"));
        Product newProduct = mock(Product.class);
        when(newProduct.getId()).thenReturn(2L);
        when(newProduct.getPrice()).thenReturn(new BigDecimal("100.00"));

        basket.addItem(product, 2);
        basket.addItem(newProduct, 1);

        assertEquals(new BigDecimal("300.00"), basket.totalCost());
    }

    @Test
    void deleteProductItem() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(1L);
        basket.addItem(product, 2);
        assertEquals(1, basket.getItems().size());

        basket.deleteProductItem(1L);

        assertEquals(0, basket.getItems().size());
    }
}

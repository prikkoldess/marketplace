package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.marketplace.order.Order;
import com.example.marketplace.order.OrderStatus;
import com.example.marketplace.product.Product;
import com.example.marketplace.user.User;

public class OrderTest {

    private User mockUser;
    private Order order;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        order = new Order(mockUser);
    }

    @Test
    void addToOrder() {
        Product product = mock(Product.class);
        when(product.getPrice()).thenReturn(new BigDecimal("100.00"));
        Product newProduct = mock(Product.class);
        when(newProduct.getPrice()).thenReturn(new BigDecimal("50.00"));

        order.addToOrder(2, product);
        order.addToOrder(1, newProduct);

        assertEquals(2, order.getItems().size());
        assertEquals(new BigDecimal("250.00"), order.getTotalAmount());
    }

    @Test
    void changeStatus() {
        assertEquals(OrderStatus.CREATED, order.getStatus());

        order.changeStatus(OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }
}

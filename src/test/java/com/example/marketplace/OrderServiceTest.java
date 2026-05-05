package com.example.marketplace;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.order.OrderRepository;
import com.example.marketplace.order.OrderService;
import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;
import com.example.marketplace.user.User;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private BasketRepository basketRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Basket basket;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        basket = new Basket(mockUser);
    }

    @Test
    void createOrder() {
        Long sellerId = 2L;
        Long buyerId = 1L;
        Long productId = 100L;
        Product product = mock(Product.class);

        basket = mock(Basket.class);
        when()
    }
}

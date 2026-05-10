package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.order.Order;
import com.example.marketplace.order.OrderRepository;
import com.example.marketplace.order.OrderService;
import com.example.marketplace.order.OrderStatus;
import com.example.marketplace.order.dto.OrderDto;
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

    @Test
    void createOrder() {
        Long buyerId = 1L;
        User buyer = mock(User.class);
        User seller = mock(User.class);
        Basket basket = new Basket(buyer);
        Product product = new Product("Apple", new BigDecimal("100.00"), 2, seller);
        Product spyProduct = spy(product);
        when(spyProduct.getId()).thenReturn(100L);
        basket.addItem(spyProduct, 2);

        when(basketRepository.findByBuyerId(buyerId)).thenReturn(Optional.of(basket));

        when(productRepository.findByIdWithLock(100L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        orderService.createOrder(buyerId);

        assertEquals(0, basket.getItems().size());
        assertEquals(0, product.getQuantity());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus() {
        Long orderId = 10L;
        Long sellerId = 1L;
        User user = mock(User.class);
        Product product = new Product("Apple", new BigDecimal("100.00"), 2, user);
        Order order = new Order(user);
        order.addToOrder(2, product);
        when(user.getId()).thenReturn(sellerId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        assertEquals(OrderStatus.CREATED, order.getStatus());

        orderService.updateOrderStatus(OrderStatus.DELIVERED, orderId, sellerId);

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void getSellerOrders() {
        Long sellerId = 1L;
        Long orderId = 10L;
        Long productId = 99L;
        User seller = mock(User.class);

        Product product = new Product("Apple", new BigDecimal("100.00"), 2, seller);
        Product spyProduct = spy(product);
        when(spyProduct.getId()).thenReturn(productId);
        Order order = new Order(seller);
        Order spyOrder = spy(order);
        when(spyOrder.getId()).thenReturn(orderId);
        spyOrder.addToOrder(2, spyProduct);

        when(orderRepository.findOrdersBySellerId(sellerId)).thenReturn(List.of(spyOrder));

        List<OrderDto> resultList = orderService.getSellerOrders(sellerId);
        assertEquals(1, resultList.size());

        OrderDto resultDto = resultList.get(0);

        assertEquals(orderId, resultDto.getId());
        assertEquals(1, resultDto.getItems().size());

    }
}

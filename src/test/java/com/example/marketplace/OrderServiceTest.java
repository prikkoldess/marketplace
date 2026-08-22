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
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.merchant.Merchant;
import com.example.marketplace.order.Order;
import com.example.marketplace.order.OrderRepository;
import com.example.marketplace.order.OrderService;
import com.example.marketplace.order.OrderStatus;
import com.example.marketplace.order.dto.OrderDto;
import com.example.marketplace.order.orderGroup.OrderGroup;
import com.example.marketplace.order.orderGroup.OrderGroupRepository;
import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;
import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private BasketRepository basketRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderGroupRepository orderGroupRepository;
    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrder() {
        Long buyerId = 1L;
        User buyer = mock(User.class);
        Merchant merchant = mock(Merchant.class);
        Basket basket = new Basket(buyer);
        Product product = new Product("Apple", new BigDecimal("100.00"), 2, merchant);
        Product spyProduct = spy(product);
        when(spyProduct.getId()).thenReturn(100L);
        when(spyProduct.getMerchant()).thenReturn(merchant);
        basket.addItem(spyProduct, 2);

        when(basketRepository.findByBuyerId(buyerId)).thenReturn(Optional.of(basket));

        when(productRepository.findByIdWithLock(100L)).thenReturn(Optional.of(product));
        when(orderGroupRepository.save(any(OrderGroup.class))).thenAnswer(i -> i.getArgument(0));

        orderService.placeOrder(buyerId);

        assertEquals(0, basket.getItems().size());
        assertEquals(0, product.getQuantity());
        verify(orderGroupRepository, times(1)).save(any(OrderGroup.class));
    }

    @Test
    void updateOrderStatus() {
        UUID orderId = UUID.randomUUID();
        Long sellerId = 1L;
        Merchant merchant = mock(Merchant.class);
        UUID merchantId = UUID.randomUUID();
        User user = mock(User.class);
        when(user.getMerchant()).thenReturn(merchant);
        when(merchant.getId()).thenReturn(merchantId);
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));
        Product product = new Product("Apple", new BigDecimal("100.00"), 2, merchant);
        Order order = new Order(user, merchant);
        order.addToOrder(2, product);

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
        UUID merchantId = UUID.randomUUID();

        Merchant merchant = mock(Merchant.class);
        when(merchant.getId()).thenReturn(merchantId);

        User seller = mock(User.class);
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        when(seller.getMerchant()).thenReturn(merchant);

        Product product = new Product("Apple", new BigDecimal("100.00"), 2, merchant);
        Product spyProduct = spy(product);
        when(spyProduct.getId()).thenReturn(productId);

        Order order = new Order(seller, merchant);
        Order spyOrder = spy(order);
        when(spyOrder.getId()).thenReturn(orderId);
        spyOrder.addToOrder(2, spyProduct);

        when(orderRepository.findOrdersByMerchantId(merchantId)).thenReturn(List.of(spyOrder));

        List<OrderDto> resultList = orderService.getSellerOrders(sellerId);

        assertEquals(1, resultList.size());
        OrderDto resultDto = resultList.get(0);
        assertEquals(orderId, resultDto.getId());
        assertEquals(1, resultDto.getItems().size());

    }
}

package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.basket.BasketService;
import com.example.marketplace.basket.dto.BasketDto;
import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;
import com.example.marketplace.user.User;

@ExtendWith(MockitoExtension.class)
public class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BasketService basketService;

    private User mockUser;
    private Basket basket;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        basket = new Basket(mockUser);
    }

    @Test
    void addToBasket() {
        Long productId = 5L;
        Long buyerId = 1L;
        Integer quantity = 10;
        Product product = mock(Product.class);

        when(basketRepository.findByBuyerId(buyerId)).thenReturn(Optional.of(basket));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        basketService.addToBasket(buyerId, productId, quantity);

        assertEquals(1, basket.getItems().size());
        assertEquals(10, basket.getItems().get(0).getQuantity());
        verify(basketRepository, times(1)).save(basket);

    }

    @Test
    void getBuyerBasket() {
        Long buyerId = 1L;
        Long productId = 100L;
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(productId);
        when(product.getTitle()).thenReturn("Apple");
        when(product.getPrice()).thenReturn(new BigDecimal("100.00"));
        when(basketRepository.findByBuyerId(buyerId)).thenReturn(Optional.of(basket));
        basket.addItem(product, 2);

        BasketDto result = basketService.getBuyerBasket(buyerId);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), result.getTotalCost());
        assertEquals(1, result.getBasketItems().size());
        assertEquals("Apple", result.getBasketItems().get(0).getProductTitle());
        assertEquals(productId, result.getBasketItems().get(0).getProductId());

    }

    @Test
    void deleteProductItem() {
        Long buyerId = 1L;
        Long productId = 100L;

        Product product = mock(Product.class);
        when(product.getId()).thenReturn(productId);
        when(basketRepository.findByBuyerId(buyerId)).thenReturn(Optional.of(basket));
        basket.addItem(product, 2);
        assertEquals(1, basket.getItems().size());

        basketService.deleteProductItem(buyerId, productId);

        assertEquals(0, basket.getItems().size());
    }
}

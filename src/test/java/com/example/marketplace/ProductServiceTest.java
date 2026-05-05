package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;
import com.example.marketplace.product.ProductService;
import com.example.marketplace.product.ProductStatus;
import com.example.marketplace.product.dto.ProductCreateDto;
import com.example.marketplace.product.dto.ProductDto;
import com.example.marketplace.user.Role;
import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    private User user;

    @BeforeEach
    void setaUp() {
        user = mock(User.class);
    }

    @Test
    void createProduct() {
        Long sellerId = 1L;
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));
        ProductCreateDto dto = new ProductCreateDto();
        dto.setTitle("Apple");
        dto.setPrice(new BigDecimal("50.00"));
        dto.setQuantity(2);
        when(user.getRole()).thenReturn(Role.SELLER);
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductDto result = productService.createProduct(dto, sellerId);

        assertEquals("Apple", result.getTitle());
        assertEquals(new BigDecimal("50.00"), result.getPrice());
        assertEquals(2, result.getQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct() {
        Long productId = 100L;
        Long sellerId = 1L;
        Product product = new Product("Apple", new BigDecimal("50.00"), 2, user);
        assertEquals(new BigDecimal("50.00"), product.getPrice());
        when(productRepository.findByIdAndSellerId(productId, sellerId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId, sellerId);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void hideProduct() {
        Long productId = 100L;
        Long sellerId = 1L;
        Product product = new Product("Apple", new BigDecimal("50.00"), 2, user);
        when(productRepository.findByIdAndSellerId(productId, sellerId)).thenReturn(Optional.of(product));
        assertEquals(ProductStatus.ACTIVE, product.getStatus());

        productService.hideProduct(productId, sellerId);

        assertEquals(ProductStatus.BLOCKED, product.getStatus());
    }
}

package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.marketplace.merchant.Merchant;
import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;
import com.example.marketplace.product.ProductService;
import com.example.marketplace.product.ProductStatus;
import com.example.marketplace.product.dto.ProductCreateDto;
import com.example.marketplace.product.dto.ProductDto;
import com.example.marketplace.product.dto.ProductUpdateDto;
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

    private Merchant merchant;

    @BeforeEach
    void setaUp() {
        user = mock(User.class);
        merchant = mock(Merchant.class);
    }

    @Test
    void createProduct() {
        Long sellerId = 1L;
        ProductCreateDto dto = new ProductCreateDto("Apple", 2, new BigDecimal("50.00"));
        when(user.getRole()).thenReturn(Role.SELLER);
        when(user.getMerchant()).thenReturn(merchant);
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductDto result = productService.createProduct(dto, sellerId);

        assertEquals("Apple", result.title());
        assertEquals(new BigDecimal("50.00"), result.price());
        assertEquals(2, result.quantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct() {
        Long productId = 100L;
        Long sellerId = 1L;
        UUID merchantId = UUID.randomUUID();

        when(merchant.getId()).thenReturn(merchantId);
        when(user.getMerchant()).thenReturn(merchant);
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));

        Product product = new Product("Apple", new BigDecimal("50.00"), 2, merchant);

        when(productRepository.findByIdAndMerchantId(productId, merchantId)).thenReturn(Optional.of(product));
        assertEquals(new BigDecimal("50.00"), product.getPrice());

        productService.deleteProduct(productId, sellerId);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void hideProduct() {
        Long productId = 100L;
        Long sellerId = 1L;
        UUID merchantId = UUID.randomUUID();
        when(merchant.getId()).thenReturn(merchantId);
        when(user.getMerchant()).thenReturn(merchant);
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));

        Product product = new Product("Apple", new BigDecimal("50.00"), 2, merchant);
        when(productRepository.findByIdAndMerchantId(productId, merchantId)).thenReturn(Optional.of(product));

        assertEquals(ProductStatus.ACTIVE, product.getStatus());

        productService.hideProduct(productId, sellerId);

        assertEquals(ProductStatus.BLOCKED, product.getStatus());
    }

    @Test
    void updateProduct() {
        Long productId = 1L;
        Long sellerId = 1L;
        UUID merchantId = UUID.randomUUID();
        ProductUpdateDto productUpdateDto = new ProductUpdateDto(10, new BigDecimal("100.00"));
        Product product = new Product("Apple", new BigDecimal("50.00"), 2, merchant);
        when(productRepository.findByIdAndMerchantId(sellerId, merchantId)).thenReturn(Optional.of(product));
        when(merchant.getId()).thenReturn(merchantId);
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));
        when(user.getMerchant()).thenReturn(merchant);
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(new BigDecimal("50.00"), product.getPrice());
        assertEquals(2, product.getQuantity());

        productService.updateProduct(productId, sellerId, productUpdateDto);

        assertEquals(new BigDecimal("100.00"), product.getPrice());
        assertEquals(10, product.getQuantity());

    }
}

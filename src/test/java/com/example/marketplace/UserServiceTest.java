package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;
import com.example.marketplace.user.Status;
import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;
import com.example.marketplace.user.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void addToWishlist() {
        Long userId = 1L;
        Long productId = 100L;
        User user = new User();
        Product product = mock(Product.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        userService.addToWishlist(userId, productId);

        assertEquals(1, user.getWishlistItems().size());

    }

    @Test
    void removeFromWishlist() {
        Long userId = 1L;
        Long productId = 100L;
        User user = new User();
        Product product = mock(Product.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        userService.addToWishlist(userId, productId);
        assertEquals(1, user.getWishlistItems().size());

        userService.removeFromWishlist(userId, productId);

        assertEquals(0, user.getWishlistItems().size());
    }

    @Test
    void blockUser() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertEquals(Status.ACTIVE, user.getStatus());

        userService.blockUserByAdmin(userId);

        assertEquals(Status.BLOCKED, user.getStatus());
    }

    @Test
    void activateUser() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.blockUserByAdmin(userId);
        assertEquals(Status.BLOCKED, user.getStatus());

        userService.activateUserByAdmin(userId);

        assertEquals(Status.ACTIVE, user.getStatus());
    }
}

package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.example.marketplace.product.Product;
import com.example.marketplace.user.Role;
import com.example.marketplace.user.Status;
import com.example.marketplace.user.User;

public class UserTest {

    @Test
    void testRegisterSeller() {
        User seller = new User("Dan", "Jones", Role.SELLER, "seller@gmail.com", "sellerpass");
        seller.setStatus(Status.BLOCKED);
        assertEquals(Status.BLOCKED, seller.getStatus());
        assertEquals(Role.SELLER, seller.getRole());
        assertEquals("Dan", seller.getFirstName());
    }

    @Test
    void testregisterBuyer() {
        User buyer = new User("Tom", "Brons", Role.BUYER, "buyer@gmail.com", "buyerpass");

        assertEquals(Role.BUYER, buyer.getRole());
        assertEquals(Status.ACTIVE, buyer.getStatus());
        assertEquals("buyerpass", buyer.getPassword());
    }

    @Test
    void addToWishlist() {
        User mockUser = new User();
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(100L);

        mockUser.addToWishlist(product);

        assertEquals(1, mockUser.getWishlistItems().size());
    }

    @Test
    void removeFromWishlist() {
        Long productId = 100L;
        User user = new User();
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(productId);
        user.addToWishlist(product);
        assertEquals(1, user.getWishlistItems().size());

        user.removeFromWishlist(product);

        assertEquals(0, user.getWishlistItems().size());

    }

    @Test
    void blockUser() {
        User user = new User();
        assertEquals(Status.ACTIVE, user.getStatus());

        user.blockUser();

        assertEquals(Status.BLOCKED, user.getStatus());
    }

    @Test
    void activateUser() {
        User user = new User();
        user.blockUser();
        assertEquals(Status.BLOCKED, user.getStatus());

        user.activateUser();

        assertEquals(Status.ACTIVE, user.getStatus());
    }
}

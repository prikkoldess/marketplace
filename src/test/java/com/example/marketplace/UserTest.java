package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.marketplace.user.Role;
import com.example.marketplace.user.Status;
import com.example.marketplace.user.User;

public class UserTest {

    @Test
    void testRegisterSeller() {
        User seller = new User("Dan", "Jones", Role.SELLER, Status.ACTIVE, "seller@gmail.com", "sellerpass");
        seller.setStatus(Status.BLOCKED);
        assertEquals(Status.BLOCKED, seller.getStatus());
        assertEquals(Role.SELLER, seller.getRole());
        assertEquals("Dan", seller.getFirstName());
    }

    @Test
    void testregisterBuyer() {
        User buyer = new User("Tom", "Brons", Role.BUYER, Status.ACTIVE, "buyer@gmail.com", "buyerpass");

        assertEquals(Role.BUYER, buyer.getRole());
        assertEquals(Status.ACTIVE, buyer.getStatus());
        assertEquals("buyerpass", buyer.getPassword());
    }

}

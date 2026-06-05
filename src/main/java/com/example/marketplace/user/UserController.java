package com.example.marketplace.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.security.UserPrincipal;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/wishlist/{productId}")
    public void addToWishlist(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long productId) {
        Long buyerId = principal.getId();
        userService.addToWishlist(buyerId, productId);
    }

    @DeleteMapping("/wishlist/{productId}")
    public void removeFromWishlist(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long productId) {
        Long buyerId = principal.getId();
        userService.removeFromWishlist(buyerId, productId);
    }
}

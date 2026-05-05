package com.example.marketplace.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.security.UserPrincipal;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/buyer/add-to-wishlist")
    public void addToWishlist(@AuthenticationPrincipal UserPrincipal principal, @RequestParam Long productId) {
        Long buyerId = principal.getId();
        userService.addToWishlist(buyerId, productId);
    }

    @DeleteMapping("/buyer/remove-from-wishlist")
    public void removeFromWishlist(@AuthenticationPrincipal UserPrincipal principal, @RequestParam Long productId) {
        Long buyerId = principal.getId();
        userService.removeFromWishlist(buyerId, productId);
    }
}

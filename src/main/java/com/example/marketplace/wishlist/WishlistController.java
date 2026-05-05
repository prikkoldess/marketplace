package com.example.marketplace.wishlist;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.security.UserPrincipal;

@RestController
@RequestMapping("wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/buyer/get-items")
    public List<WishlistDto> getWishListItems(@AuthenticationPrincipal UserPrincipal principal) {
        Long buyerId = principal.getId();
        return wishlistService.getWishListItems(buyerId);
    }
}

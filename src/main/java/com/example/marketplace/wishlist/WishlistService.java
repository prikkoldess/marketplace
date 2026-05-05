package com.example.marketplace.wishlist;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public List<WishlistDto> getWishListItems(Long buyerId) {
        List<Wishlist> wishlist = wishlistRepository.findByBuyerId(buyerId);

        return wishlist.stream()
                .map(this::mapToDto)
                .toList();
    }

    private WishlistDto mapToDto(Wishlist wishlist) {
        return new WishlistDto(
                wishlist.getProduct().getId(),
                wishlist.getProduct().getTitle(),
                wishlist.getProduct().getPrice());

    }

}

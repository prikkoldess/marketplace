package com.example.marketplace.wishlist;

import java.math.BigDecimal;

public record WishlistDto(
        Long productId,
        String productTitle,
        BigDecimal currentPrice) {

}

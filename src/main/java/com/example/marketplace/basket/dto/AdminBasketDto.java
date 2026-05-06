package com.example.marketplace.basket.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.marketplace.user.User;

public record AdminBasketDto(
        User buyer,
        List<BasketItemDto> basketItems,
        BigDecimal totalCost) {

}

package com.example.marketplace.basket.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BasketItemDto {
    private Long productId;
    private String productTitle;
    private BigDecimal price;
    private Integer quantity;
}

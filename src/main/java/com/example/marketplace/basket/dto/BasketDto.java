package com.example.marketplace.basket.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class BasketDto {
    private List<BasketItemDto> basketItems;
    private BigDecimal totalCost;
}

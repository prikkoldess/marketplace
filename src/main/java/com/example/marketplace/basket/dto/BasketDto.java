package com.example.marketplace.basket.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.marketplace.basketitem.BasketItem;

import lombok.Data;

@Data
public class BasketDto {
    private List<BasketItem> basketItems;
    private BigDecimal totalCost;
}

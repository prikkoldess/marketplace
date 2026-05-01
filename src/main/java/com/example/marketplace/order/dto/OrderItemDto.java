package com.example.marketplace.order.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private String productTitle;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}

package com.example.marketplace.order.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class CheckoutGroupDto {
    private UUID checkoutGroupId;
    private List<OrderDto> orders;
    private BigDecimal totalAmount;
    private String globalStatus; // Тот самый вычисляемый статус
}

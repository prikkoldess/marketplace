package com.example.marketplace.order.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.example.marketplace.order.OrderStatus;

import lombok.Data;

@Data
public class CheckoutGroupDto {
    private UUID orderGroupId;
    private List<OrderItemDto> orders;
    private BigDecimal totalAmount;
    private OrderStatus status;
}

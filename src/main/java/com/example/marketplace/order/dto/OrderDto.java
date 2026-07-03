package com.example.marketplace.order.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.marketplace.order.OrderStatus;
import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private OrderStatus status;

}

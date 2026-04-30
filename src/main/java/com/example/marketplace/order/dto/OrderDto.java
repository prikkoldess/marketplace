package com.example.marketplace.order.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.marketplace.order.OrderItem;
import com.example.marketplace.order.OrderStatus;
import com.example.marketplace.user.User;

import lombok.Data;

@Data
public class OrderDto {
    private User buyer;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;

}

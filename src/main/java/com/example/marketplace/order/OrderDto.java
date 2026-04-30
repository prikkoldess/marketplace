package com.example.marketplace.order;

import java.util.List;

import com.example.marketplace.user.User;

import lombok.Data;

@Data
public class OrderDto {
    private User buyer;
    private OrderStatus status;
    private List<OrderItem> items;
}

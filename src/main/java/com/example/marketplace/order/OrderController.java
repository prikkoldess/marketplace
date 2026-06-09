package com.example.marketplace.order;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.order.dto.CheckoutGroupDto;
import com.example.marketplace.order.dto.OrderDto;
import com.example.marketplace.order.orderGroup.OrderGroupService;
import com.example.marketplace.security.UserPrincipal;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private OrderGroupService orderGroupService;

    public OrderController(OrderService orderService, OrderGroupService orderGroupService) {
        this.orderService = orderService;
        this.orderGroupService = orderGroupService;
    }

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public UUID placeOrder(@AuthenticationPrincipal UserPrincipal buyer) {
        Long buyerId = buyer.getId();
        UUID order = orderService.placeOrder(buyerId);
        return order;
    }

    @GetMapping("/sales")
    @PreAuthorize("hasRole('SELLER')")
    public List<OrderDto> getSellerOrders(@AuthenticationPrincipal UserPrincipal seller) {
        Long sellerId = seller.getId();
        return orderService.getSellerOrders(sellerId);
    }

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public List<CheckoutGroupDto> getBuyersOrder(@AuthenticationPrincipal UserPrincipal buyer) {
        Long buyerId = buyer.getId();
        return orderGroupService.getBuyersOrder(buyerId);
    }
}

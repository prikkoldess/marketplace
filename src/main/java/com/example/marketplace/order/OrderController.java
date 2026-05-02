package com.example.marketplace.order;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.order.dto.CheckoutGroupDto;
import com.example.marketplace.order.dto.OrderDto;
import com.example.marketplace.security.UserPrincipal;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/buyer/order")
    public void createOrder(@AuthenticationPrincipal com.example.marketplace.security.UserPrincipal buyer) {
        Long buyerId = buyer.getId();
        orderService.createOrder(buyerId);
    }

    @GetMapping("/seller/get-orders")
    public List<OrderDto> getSellerOrders(@AuthenticationPrincipal UserPrincipal seller) {
        Long sellerId = seller.getId();
        return orderService.getSellerOrders(sellerId);
    }

    @GetMapping("/buyer/get-orders")
    public List<CheckoutGroupDto> getBuyerOrders(@AuthenticationPrincipal UserPrincipal buyer) {
        Long buyerId = buyer.getId();
        return orderService.getBuyerOrders(buyerId);
    }

}

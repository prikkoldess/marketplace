package com.example.marketplace.order.orderGroup;

import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.security.UserPrincipal;

@RestController
@RequestMapping("orders")
public class OrderGroupController {
    public final OrderGroupService orderGroupService;

    public OrderGroupController(OrderGroupService orderGroupService) {
        this.orderGroupService = orderGroupService;
    }

    @PostMapping("{id}/cancel")
    public void cancelOrder(@PathVariable("id") UUID orderId,
            @AuthenticationPrincipal UserPrincipal user) {
        Long userId = user.getId();
        orderGroupService.cancelOrder(orderId, userId);
    }
}

package com.example.marketplace.order.orderGroup;

import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.Merchant.Merchant;
import com.example.marketplace.config.RabbitMQConfig;
import com.example.marketplace.notification.order.CancelOrderNotificationDto;
import com.example.marketplace.order.Order;
import com.example.marketplace.order.dto.CheckoutGroupDto;
import com.example.marketplace.order.dto.OrderItemDto;
import com.example.marketplace.product.Product;

@Service
public class OrderGroupService {

    private final OrderGroupRepository orderGroupRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderGroupService(OrderGroupRepository orderGroupRepository, RabbitTemplate rabbitTemplate) {
        this.orderGroupRepository = orderGroupRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public List<CheckoutGroupDto> getBuyersOrder(Long buyerId) {
        List<OrderGroup> orders = orderGroupRepository.findByBuyerId(buyerId);
        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }

    public CheckoutGroupDto mapToDto(OrderGroup orderGroup) {
        CheckoutGroupDto dto = new CheckoutGroupDto();
        dto.setOrderGroupId(orderGroup.getId());
        List<OrderItemDto> allOrders = orderGroup.getOrders().stream().flatMap(order -> order.getItems().stream())
                .map(item -> {
                    OrderItemDto itemDto = new OrderItemDto();
                    Product product = item.getProduct();

                    itemDto.setProductId(product.getId());
                    itemDto.setProductTitle(product.getTitle());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
                    return itemDto;
                }).toList();
        dto.setOrders(allOrders);
        dto.setTotalAmount(orderGroup.getTotalAmount());
        dto.setStatus(orderGroup.getStatus());
        return dto;
    }

    @Transactional
    public void cancelOrder(UUID orderId, Long userId) {
        OrderGroup order = orderGroupRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getBuyer().getId().equals(userId)) {
            throw new AccessDeniedException("You cannot cancel someone else's order.");
        }

        order.cancelOrder();
        for (Order orders : order.getOrders()) {
            Merchant merchant = orders.getMerchant();
            CancelOrderNotificationDto dto = new CancelOrderNotificationDto(
                    merchant.getId(),
                    UUID.randomUUID(),
                    orders.getId());

            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ORDER, RabbitMQConfig.ROUTING_KEY_CANCEL_ORDER, dto);
        }
    }
}

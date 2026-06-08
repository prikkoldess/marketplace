package com.example.marketplace.order.orderGroup;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.order.dto.CheckoutGroupDto;
import com.example.marketplace.order.dto.OrderItemDto;
import com.example.marketplace.product.Product;

@Service
public class OrderGroupService {

    private final OrderGroupRepository orderGroupRepository;

    public OrderGroupService(OrderGroupRepository orderGroupRepository) {
        this.orderGroupRepository = orderGroupRepository;
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
}

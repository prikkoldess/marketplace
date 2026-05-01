package com.example.marketplace.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.basket.basketitem.BasketItem;
import com.example.marketplace.order.dto.CheckoutGroupDto;
import com.example.marketplace.order.dto.OrderDto;
import com.example.marketplace.order.dto.OrderItemDto;
import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;
import com.example.marketplace.user.User;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, BasketRepository basketRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.basketRepository = basketRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void createOrder(Long buyerId) {
        Basket basket = basketRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        if (basket.getItems().isEmpty()) {
            throw new IllegalStateException("The basket is empty");
        }

        Map<User, List<BasketItem>> itemsBySeller = basket.getItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getSeller()));

        List<Order> createOrders = new ArrayList<>();

        UUID currentCheckoutGroupId = UUID.randomUUID();

        for (Map.Entry<User, List<BasketItem>> entry : itemsBySeller.entrySet()) {

            List<BasketItem> sellerItems = entry.getValue();

            Order sellerOrder = new Order(basket.getBuyer());
            sellerOrder.setCheckoutGroupId(currentCheckoutGroupId);

            for (BasketItem basketItem : sellerItems) {
                Product product = basketItem.getProduct();

                product.decreaseQuantity(basketItem.getQuantity());
                sellerOrder.addToOrder(basketItem.getQuantity(), product);

            }

            createOrders.add(orderRepository.save(sellerOrder));
        }

        basket.clearBasket();
    }

    public void updateOrderStatus(OrderStatus newStatus, Long orderId, Long sellerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        boolean isOwner = order.getItems().stream().findFirst()
                .map(items -> items.getProduct().getSeller().getId().equals(sellerId))
                .orElse(false);

        if (!isOwner) {
            throw new SecurityException("You can only update your own orders");
        }

        order.changeStatus(newStatus);

    }

    public List<OrderDto> getSellerOrders(Long sellerId) {
        List<Order> orders = orderRepository.findOrdersBySellerId(sellerId);
        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }

    public OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        List<OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductTitle(item.getProduct().getTitle());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
            return itemDto;
        }).toList();
        dto.setCheckoutGroupId(order.getCheckoutGroupId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setItems(itemDtos);
        return dto;
    }

    public CheckoutGroupDto mapToBuyerDto(List<Order> orders, UUID groupId) {
        CheckoutGroupDto groupDto = new CheckoutGroupDto();
        groupDto.setCheckoutGroupId(groupId);

        List<OrderDto> orderDtos = orders.stream()
                .map(this::mapToDto)
                .toList();

        groupDto.setOrders(orderDtos);

        BigDecimal total = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        groupDto.setTotalAmount(total);
        groupDto.setGlobalStatus(calculateGlobalStatus(orderDtos));
        return groupDto;
    }

    public String calculateGlobalStatus(List<OrderDto> orders) {
        boolean allDelivered = orders.stream().allMatch(o -> o.getStatus() == OrderStatus.DELIVERED);
        boolean anyShipped = orders.stream().anyMatch(o -> o.getStatus() == OrderStatus.SHIPPED);
        boolean allCancelled = orders.stream().allMatch(o -> o.getStatus() == OrderStatus.CANCELLED);
        boolean anyCancelled = orders.stream().anyMatch(o -> o.getStatus() == OrderStatus.CANCELLED);

        if (anyCancelled)
            return "There are cancelled positions";
        if (allDelivered)
            return "Delivered";
        if (allCancelled)
            return "Cancelled";
        if (anyShipped)
            return "Partially sent";
        return "In Processing / Being Collected";
    }
}

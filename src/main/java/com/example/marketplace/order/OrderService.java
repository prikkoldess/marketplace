package com.example.marketplace.order;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.Merchant.Merchant;
import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.basket.basketItem.BasketItem;
import com.example.marketplace.order.dto.OrderDto;
import com.example.marketplace.order.dto.OrderItemDto;
import com.example.marketplace.order.orderGroup.OrderGroup;
import com.example.marketplace.order.orderGroup.OrderGroupRepository;
import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;
import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final OrderGroupRepository orderGroupRepository;

    public OrderService(OrderRepository orderRepository, BasketRepository basketRepository,
            ProductRepository productRepository, UserRepository userRepository,
            OrderGroupRepository orderGroupRepository) {
        this.orderRepository = orderRepository;
        this.basketRepository = basketRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderGroupRepository = orderGroupRepository;
    }

    @Transactional
    public UUID placeOrder(Long buyerId) {
        Basket basket = basketRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        if (basket.getItems().isEmpty()) {
            throw new IllegalStateException("The basket is empty");
        }

        User buyer = basket.getBuyer();
        OrderGroup orderGroup = new OrderGroup(buyer);

        Map<Merchant, List<BasketItem>> itemsByMerchant = basket.getItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getMerchant()));

        for (Map.Entry<Merchant, List<BasketItem>> entry : itemsByMerchant.entrySet()) {
            Merchant merchant = entry.getKey();

            List<BasketItem> sortedItems = entry.getValue().stream()
                    .sorted(Comparator.comparing(i -> i.getProduct().getId()))
                    .toList();

            Order merchantOrder = new Order(basket.getBuyer(), merchant);

            for (BasketItem basketItem : sortedItems) {
                Product lockedProduct = productRepository.findByIdWithLock(basketItem.getProduct().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));

                lockedProduct.decreaseQuantity(basketItem.getQuantity());
                merchantOrder.addToOrder(basketItem.getQuantity(), lockedProduct);

            }

            orderGroup.addOrder(merchantOrder);

        }
        orderGroupRepository.save(orderGroup);
        basket.clearBasket();

        return orderGroup.getId();
    }

    @Transactional
    public void updateOrderStatus(OrderStatus newStatus, Long orderId, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (seller.getMerchant() == null || !order.getMerchant().getId().equals(seller.getMerchant().getId())) {
            throw new SecurityException("You can only update your own merchant's orders");
        }

        order.changeStatus(newStatus);

    }

    @Transactional
    public void updateOrderStatusForAdmin(OrderStatus newStatus, Long orderId, Long sellerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.changeStatus(newStatus);

    }

    public List<OrderDto> getSellerOrders(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        List<Order> orders = orderRepository.findOrdersByMerchantId(seller.getMerchant().getId());
        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }

    public OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        List<OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
            OrderItemDto itemDto = new OrderItemDto();
            Product product = item.getProduct();

            itemDto.setProductId(product.getId());
            itemDto.setProductTitle(product.getTitle());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
            return itemDto;
        }).toList();
        dto.setOrderGroupId(order.getOrderGroup().getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setItems(itemDtos);
        return dto;
    }

    public String calculateGlobalStatus(List<OrderDto> orders) {
        boolean allDelivered = orders.stream().allMatch(o -> o.getStatus() == OrderStatus.DELIVERED);
        boolean anyShipped = orders.stream().anyMatch(o -> o.getStatus() == OrderStatus.SHIPPED);
        boolean allCancelled = orders.stream().allMatch(o -> o.getStatus() == OrderStatus.CANCELLED);
        boolean anyCancelled = orders.stream().anyMatch(o -> o.getStatus() == OrderStatus.CANCELLED);

        if (allCancelled)
            return "Cancelled";
        if (allDelivered)
            return "Delivered";
        if (anyCancelled)
            return "There are cancelled positions";
        if (anyShipped)
            return "Partially sent";
        return "In Processing / Being Collected";
    }
}

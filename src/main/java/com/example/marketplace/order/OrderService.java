package com.example.marketplace.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.basket.basketitem.BasketItem;
import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;

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
    public Order createOrder(Long buyerId) {
        Basket basket = basketRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        if (basket.getItems().isEmpty()) {
            throw new IllegalStateException("The basket is empty");
        }
        Order order = new Order(basket.getBuyer());
        for (BasketItem basketItem : basket.getItems()) {
            Product product = basketItem.getProduct();

            if (product.getQuantity() < basketItem.getQuantity()) {
                throw new IllegalArgumentException("Product " + product.getTitle() + " not enough");
            }

            product.updateProduct(product.getQuantity() - basketItem.getQuantity(), null);
            order.addToOrder(basketItem.getQuantity(), product);

        }
        basket.clearBasket();

        return orderRepository.save(order);
    }

}

package com.example.marketplace.order;

import org.springframework.stereotype.Service;

import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.basketitem.BasketItem;
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

    public Order createOrder(Long buyerId) {
        Basket basket = basketRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        Order order = new Order(basket.getBuyer());
        for (BasketItem basketItem : basket.getItems()) {
            Product product = basketItem.getProduct();

            if (product.getQuantity() < basketItem.getQuantity()) {
                throw new IllegalArgumentException("Product " + product.getTitle() + " not enough");
            }

            product.updateProduct(product.getQuantity() - basketItem.getQuantity(), null);
            order.addToOrder(basketItem.getQuantity(), product);

            basket.clearBasket();
            basketRepository.save(basket);
            return orderRepository.save(order);
        }
    }

}

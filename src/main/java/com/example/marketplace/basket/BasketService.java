package com.example.marketplace.basket;

import org.springframework.stereotype.Service;

import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;

@Service
public class BasketService {
    private BasketRepository basketRepository;
    private ProductRepository productRepository;

    public BasketService(BasketRepository basketRepository, ProductRepository productRepository) {
        this.basketRepository = basketRepository;
        this.productRepository = productRepository;
    }

    public void addToBasket(Long buyerId, Long productId, Integer quantity) {
        Basket basket = basketRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Basket not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        basket.addItem(product, quantity);
        basketRepository.save(basket);
    }
}

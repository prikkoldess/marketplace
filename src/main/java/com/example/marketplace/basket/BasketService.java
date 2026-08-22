package com.example.marketplace.basket;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.basket.basketItem.BasketItem;
import com.example.marketplace.basket.dto.AdminBasketDto;
import com.example.marketplace.basket.dto.BasketDto;
import com.example.marketplace.basket.dto.BasketItemDto;

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

    @Transactional
    public void addToBasket(Long buyerId, Long productId, Integer quantity) {
        Basket basket = basketRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Basket not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int quantityAlreadyInBasket = basket.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId)).map(BasketItem::getQuantity).findFirst()
                .orElse(0);

        if (product.getQuantity() < (quantity + quantityAlreadyInBasket)) {
            throw new IllegalStateException("Not enough stock. You already have "
                    + quantityAlreadyInBasket + " in basket. Available total: " + product.getQuantity());
        }

        basket.addItem(product, quantity);
        basketRepository.save(basket);
    }

    public BasketDto getBuyerBasket(Long buyerId) {
        Basket basket = basketRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Basket not found"));
        return mapToDto(basket);
    }

    @Transactional
    public void deleteProductItem(Long buyerId, Long productId) {
        Basket basket = basketRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Basket not found"));
        basket.deleteProductItem(productId);

    }

    ///// ADMIN/////
    public AdminBasketDto getUserBasketByAdmin(Long userId) {
        Basket basket = basketRepository.findByBuyerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Basket not found"));
        return mapToAdminDto(basket);

    }

    public BasketDto mapToDto(Basket basket) {
        BasketDto dto = new BasketDto();

        List<BasketItemDto> itemDtos = basket.getItems().stream().map(item -> {
            BasketItemDto itemDto = new BasketItemDto();

            Product product = item.getProduct();
            itemDto.setProductId(product.getId());
            itemDto.setProductTitle(product.getTitle());
            itemDto.setPrice(product.getPrice());
            itemDto.setQuantity(item.getQuantity());

            return itemDto;
        }).toList();

        dto.setBasketItems(itemDtos);
        dto.setTotalCost(basket.totalCost());
        return dto;
    }

    private AdminBasketDto mapToAdminDto(Basket basket) {
        List<BasketItemDto> itemDtos = basket.getItems().stream().map(item -> {
            BasketItemDto itemDto = new BasketItemDto();
            Product product = item.getProduct();

            itemDto.setProductId(product.getId());
            itemDto.setProductTitle(product.getTitle());
            itemDto.setPrice(product.getPrice());
            itemDto.setQuantity(item.getQuantity());

            return itemDto;
        }).toList();

        return new AdminBasketDto(
                basket.getBuyer(),
                itemDtos,
                basket.totalCost());
    }
}

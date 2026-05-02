package com.example.marketplace.basket;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.basket.dto.BasketDto;
import com.example.marketplace.security.UserPrincipal;

@RestController
@RequestMapping("basket")
public class BasketController {
    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @PostMapping("/buyer/add-product")
    public void addToBasket(@RequestParam Long product, Integer quantity,
            @AuthenticationPrincipal UserPrincipal buyer) {

        Long buyerId = buyer.getId();
        basketService.addToBasket(buyerId, product, quantity);
    }

    @GetMapping("/buyer/prodacts")
    public BasketDto getBuyerBasket(@AuthenticationPrincipal UserPrincipal buyer) {
        Long buyerId = buyer.getId();
        return basketService.getBuyerBasket(buyerId);
    }

    @DeleteMapping("/buyer/delete/product")
    public void deleteProductItem(@AuthenticationPrincipal UserPrincipal buyer, @RequestParam Long productId) {
        Long buyerId = buyer.getId();
        basketService.deleteProductItem(buyerId, productId);
    }

}

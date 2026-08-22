package com.example.marketplace.admin;

import java.util.List;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.marketplace.basket.BasketService;
import com.example.marketplace.basket.dto.AdminBasketDto;
import com.example.marketplace.order.OrderService;
import com.example.marketplace.order.OrderStatus;
import com.example.marketplace.order.dto.OrderDto;
import com.example.marketplace.product.ProductService;
import com.example.marketplace.product.dto.ProductDto;
import com.example.marketplace.user.UserService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("admin")
public class AdminController {
    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    private final BasketService basketService;

    public AdminController(OrderService orderService, UserService userService, BasketService basketService,
            ProductService productService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
        this.basketService = basketService;
    }

    @GetMapping("/users/{userId}/baskets")
    public AdminBasketDto getUserBasketByAdmin(@PathVariable Long userId) {
        return basketService.getUserBasketByAdmin(userId);
    }

    @GetMapping("/user/{userId}/sales")
    public List<OrderDto> getSellerOrders(@PathVariable Long userId) {
        return orderService.getSellerOrders(userId);
    }

    @PatchMapping("/users/{userId}/orders/{orderId}/status")
    public void updateOrderStatusForAdmin(
            @RequestParam OrderStatus status,
            @PathVariable UUID orderId,
            @PathVariable Long userId) {

        orderService.updateOrderStatusByAdmin(status, orderId, userId);
    }

    @PatchMapping("/users/{userId}/block")
    public void blockUserByAdmin(@PathVariable Long userId) {
        userService.blockUserByAdmin(userId);
    }

    @PatchMapping("/users/{userId}/activate")
    public void activateUserByAdmin(@PathVariable Long userId) {
        userService.activateUserByAdmin(userId);
    }

    @GetMapping("users/{userId}/products")
    public Page<ProductDto> getAllSellerProduct(@PathVariable Long userId,
            @ParameterObject @PageableDefault(size = 20, page = 0) Pageable pageable) {
        return productService.getAllSellerProduct(userId, pageable);
    }

    @GetMapping("/products")
    public Page<ProductDto> getAllProducts(@ParameterObject @PageableDefault(size = 20, page = 0) Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @PatchMapping("/product/{productId}/hide")
    public void hideProductByAdmin(@PathVariable Long productId) {
        productService.hideProductByAdmin(productId);
    }

    @PatchMapping("/product/{productId}/unlock")
    public void unlockProductByAdmin(@PathVariable Long productId) {
        productService.unlockProductByAdmin(productId);
    }

    @DeleteMapping("/product/{productId}")
    public void deleteProductByAdmin(@PathVariable Long productId) {
        productService.deleteProductByAdmin(productId);
    }

}
